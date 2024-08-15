package fete.be.domain.poster.application;

import fete.be.domain.event.persistence.*;
import fete.be.domain.member.application.MemberService;
import fete.be.domain.member.persistence.Member;
import fete.be.domain.admin.application.dto.request.ApprovePostersRequest;
import fete.be.domain.poster.application.dto.request.ModifyPosterRequest;
import fete.be.domain.poster.application.dto.request.WritePosterRequest;
import fete.be.domain.poster.application.dto.response.PosterDto;
import fete.be.domain.poster.persistence.*;
import fete.be.global.util.ResponseMessage;
import fete.be.global.util.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PosterService {

    private final PosterRepository posterRepository;
    private final EventRepository eventRepository;
    private final PosterLikeRepository posterLikeRepository;
    private final MemberService memberService;

    @Transactional
    public Long writePoster(Member member, WritePosterRequest request) {
        Poster poster = Poster.createPoster(member, request);
        Poster savedPoster = posterRepository.save(poster);

        Event event = Event.createEvent(savedPoster, request.getEvent());
        eventRepository.save(event);

        return savedPoster.getPosterId();
    }

    public Poster findPosterByPosterId(Long posterId) {
        return posterRepository.findById(posterId).orElseThrow(
                () -> new IllegalArgumentException(ResponseMessage.POSTER_INVALID_POSTER.getMessage()));
    }

    @Transactional
    public Long updatePoster(Long posterId, ModifyPosterRequest request) {
        // 현재 API 요청을 보낸 Member 찾기
        Member member = memberService.findMemberByEmail();

        // posterId로 포스터 찾기
        Poster poster = findPosterByPosterId(posterId);

        // 수정을 요청한 사용자가 해당 포스터의 작성자가 아닌 경우
        if (!poster.getMember().equals(member)) {
            throw new IllegalArgumentException(ResponseMessage.POSTER_INVALID_USER.getMessage());
        }

        // 업데이트 진행
        Poster updatePoster = Poster.updatePoster(poster, request);
        posterRepository.save(updatePoster);

        return updatePoster.getPosterId();
    }

    @Transactional
    public Long deletePoster(Long posterId) {
        // 현재 API 요청을 보낸 Member 찾기
        Member member = memberService.findMemberByEmail();

        // posterId로 포스터 찾기
        Poster poster = findPosterByPosterId(posterId);

        // 삭제를 요청한 사용자가 해당 포스터의 작성자가 아닌 경우
        if (!poster.getMember().equals(member)) {
            throw new IllegalArgumentException(ResponseMessage.POSTER_INVALID_USER.getMessage());
        }

        // 소프트 삭제 실행 (status 필드를 DELETE로 변경)
        Poster deletePoster = Poster.deletePoster(poster);
        return deletePoster.getPosterId();
    }

    public Page<PosterDto> getPosters(Status status, int page, int size) {
        // Member 정보
        Member member = memberService.findMemberByEmail();

        // 페이징 조건 추가
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Order.asc("event.startDate"),  // 첫 번째 정렬 기준: 이벤트 시작 날짜
                        Sort.Order.asc("title")  // 두 번째 정렬 기준: 이벤트 이름
                )
        );

        // 조건에 맞는 Poster 가져오기
        return posterRepository.findByStatus(status, pageable)
                .map(poster -> {
                    boolean isLike = posterLikeRepository.findByMemberIdAndPosterId(member.getMemberId(), poster.getPosterId()).isPresent();

                    return new PosterDto(
                            poster.getPosterId(),
                            poster.getTitle(),
                            poster.getPosterImages().stream()
                                    .map(PosterImage::getImageUrl)
                                    .collect(Collectors.toList()),
                            poster.getInstitution(),
                            poster.getEvent().getEventType(),
                            poster.getEvent().getEventName(),
                            poster.getEvent().getStartDate(),
                            poster.getEvent().getEndDate(),
                            poster.getEvent().getAddress(),
                            poster.getEvent().getTickets().stream()
                                    .map(ticket -> new TicketInfoDto(
                                            ticket.getTicketType(),
                                            ticket.getTicketPrice(),
                                            ticket.getMaxTicketCount()
                                    ))
                                    .collect(Collectors.toList()),
                            poster.getEvent().getDescription(),
                            poster.getEvent().getGenre(),
                            poster.getEvent().getHomepageUrl(),
                            poster.getEvent().getArtists().stream()
                                    .map(artist -> new ArtistDto(
                                            artist.getName(),
                                            artist.getImageUrl()
                                    ))
                                    .collect(Collectors.toList()),
                            isLike,
                            poster.getLikeCount()
                    );
                });
    }

    public PosterDto getPoster(Long posterId, Status status) {
        // Member 정보
        Member member = memberService.findMemberByEmail();

        // posterId로 해당 Poster 찾아오기
        Poster poster = posterRepository.findByStatusAndPosterId(status, posterId).orElseThrow(
                () -> new IllegalArgumentException(ResponseMessage.POSTER_INVALID_POSTER.getMessage())
        );

        // 관심 등록 상태
        boolean isLike = posterLikeRepository.findByMemberIdAndPosterId(member.getMemberId(), poster.getPosterId()).isPresent();


        // 찾은 Poster를 PosterDto에 담기
        PosterDto posterDto = new PosterDto(
                poster.getPosterId(),
                poster.getTitle(),
                poster.getPosterImages().stream()
                        .map(PosterImage::getImageUrl)
                        .collect(Collectors.toList()),
                poster.getInstitution(),
                poster.getEvent().getEventType(),
                poster.getEvent().getEventName(),
                poster.getEvent().getStartDate(),
                poster.getEvent().getEndDate(),
                poster.getEvent().getAddress(),
                poster.getEvent().getTickets().stream()
                        .map(ticket -> new TicketInfoDto(
                                ticket.getTicketType(),
                                ticket.getTicketPrice(),
                                ticket.getMaxTicketCount()
                        ))
                        .collect(Collectors.toList()),
                poster.getEvent().getDescription(),
                poster.getEvent().getGenre(),
                poster.getEvent().getHomepageUrl(),
                poster.getEvent().getArtists().stream()
                        .map(artist -> new ArtistDto(
                                artist.getName(),
                                artist.getImageUrl()
                        ))
                        .collect(Collectors.toList()),
                isLike,
                poster.getLikeCount()
        );

        return posterDto;
    }

    @Transactional
    public void approvePosters(ApprovePostersRequest request) {
        List<Long> posterIds = request.getPosterIds();
        List<Poster> posters = posterRepository.findAllById(posterIds);  // posterIds로 한번에 대량 조회

        // 승인 요청한 포스터의 수와 찾은 포스터의 수가 일치하지 않는 경우
        if (posters.size() != posterIds.size()) {
            throw new IllegalArgumentException(ResponseMessage.POSTER_INVALID_POSTER.getMessage());
        }

        for (Poster poster : posters) {
            Poster.approvePoster(poster);  // 관리자 승인 메서드 실행
        }
    }

    public Page<PosterDto> getMyPosters(int page, int size) {
        // 페이징 조건 추가
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Order.asc("event.startDate"),  // 첫 번째 정렬 기준: 이벤트 시작 날짜
                        Sort.Order.asc("title")  // 두 번째 정렬 기준: 이벤트 이름
                )
        );

        // Member 찾기
        Member member = memberService.findMemberByEmail();

        // 조건에 맞는 데이터 조회
        return posterRepository.findByMember(member, pageable)
                .map(poster -> {
                    boolean isLike = posterLikeRepository.findByMemberIdAndPosterId(member.getMemberId(), poster.getPosterId()).isPresent();

                    return new PosterDto(
                            poster.getPosterId(),
                            poster.getTitle(),
                            poster.getPosterImages().stream()
                                    .map(PosterImage::getImageUrl)
                                    .collect(Collectors.toList()),
                            poster.getInstitution(),
                            poster.getEvent().getEventType(),
                            poster.getEvent().getEventName(),
                            poster.getEvent().getStartDate(),
                            poster.getEvent().getEndDate(),
                            poster.getEvent().getAddress(),
                            poster.getEvent().getTickets().stream()
                                    .map(ticket -> new TicketInfoDto(
                                            ticket.getTicketType(),
                                            ticket.getTicketPrice(),
                                            ticket.getMaxTicketCount()
                                    ))
                                    .collect(Collectors.toList()),
                            poster.getEvent().getDescription(),
                            poster.getEvent().getGenre(),
                            poster.getEvent().getHomepageUrl(),
                            poster.getEvent().getArtists().stream()
                                    .map(artist -> new ArtistDto(
                                            artist.getName(),
                                            artist.getImageUrl()
                                    ))
                                    .collect(Collectors.toList()),
                            isLike,
                            poster.getLikeCount()
                    );
                });
    }

    @Transactional
    public void likePoster(Long posterId, Boolean isLike) {
        // Member 찾기
        Member member = memberService.findMemberByEmail();
        // posterId로 포스터 찾기
        Poster poster = findPosterByPosterId(posterId);
        // 조건에 해당하는 데이터 찾기
        Optional<PosterLike> find = posterLikeRepository.findByMemberIdAndPosterId(member.getMemberId(), poster.getPosterId());

        if (isLike) {  // 관심 등록
            if (!find.isPresent()) {
                PosterLike posterLike = PosterLike.createPosterLike(member.getMemberId(), poster.getPosterId());
                posterLikeRepository.save(posterLike);
                Poster.likePoster(poster, 1);
            }
        } else {  // 관심 해제
            if (find.isPresent()) {
                PosterLike posterLike = find.get();
                posterLikeRepository.delete(posterLike);
                Poster.likePoster(poster, -1);
            }
        }
    }

    public Page<PosterDto> getLikePosters(int page, int size) {
        // 페이징 조건 추가
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Order.asc("event.startDate"),  // 첫 번째 정렬 기준: 이벤트 시작 날짜
                        Sort.Order.asc("title")  // 두 번째 정렬 기준: 이벤트 이름
                )
        );

        // Member 찾기
        Member member = memberService.findMemberByEmail();

        // memberId로 PosterLike 조회
        List<PosterLike> myLikes = posterLikeRepository.findPosterLikesByMemberId(member.getMemberId());

        // myLikes에서 posterId만 추출
        List<Long> posterIds = myLikes.stream()
                .map(PosterLike::getPosterId)
                .collect(Collectors.toList());

        // 페이징 반영해서 조회
        return posterRepository.findByPosterIdIn(posterIds, pageable)
                .map(poster ->
                        new PosterDto(
                                poster.getPosterId(),
                                poster.getTitle(),
                                poster.getPosterImages().stream()
                                        .map(PosterImage::getImageUrl)
                                        .collect(Collectors.toList()),
                                poster.getInstitution(),
                                poster.getEvent().getEventType(),
                                poster.getEvent().getEventName(),
                                poster.getEvent().getStartDate(),
                                poster.getEvent().getEndDate(),
                                poster.getEvent().getAddress(),
                                poster.getEvent().getTickets().stream()
                                        .map(ticket -> new TicketInfoDto(
                                                ticket.getTicketType(),
                                                ticket.getTicketPrice(),
                                                ticket.getMaxTicketCount()
                                        ))
                                        .collect(Collectors.toList()),
                                poster.getEvent().getDescription(),
                                poster.getEvent().getGenre(),
                                poster.getEvent().getHomepageUrl(),
                                poster.getEvent().getArtists().stream()
                                        .map(artist -> new ArtistDto(
                                                artist.getName(),
                                                artist.getImageUrl()
                                        ))
                                        .collect(Collectors.toList()),
                                true,
                                poster.getLikeCount()
                        )
                );
    }

    // 매일 자정마다 실행
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void checkEndedPoster() {
        // 종료된 포스터 조회
        List<Poster> endedPosters = posterRepository.findEndDateBeforeNow(LocalDateTime.now());

        // 종료된 포스터의 status를 END로 변경
        for (Poster poster : endedPosters) {
            Poster.endPoster(poster);
        }
    }
}
