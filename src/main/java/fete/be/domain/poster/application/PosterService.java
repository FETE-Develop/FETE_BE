package fete.be.domain.poster.application;

import fete.be.domain.member.application.MemberService;
import fete.be.domain.member.persistence.Member;
import fete.be.domain.poster.application.dto.request.ApprovePostersRequest;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PosterService {

    private final PosterRepository posterRepository;
    private final PosterLikeRepository posterLikeRepository;
    private final MemberService memberService;

    @Transactional
    public Long writePoster(Member member, WritePosterRequest request) {
        Poster poster = Poster.createPoster(member, request);
        Poster savedPoster = posterRepository.save(poster);
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
                .map(poster -> new PosterDto(
                        poster.getPosterId(),
                        poster.getTitle(),
                        poster.getPosterImages().stream()
                                .map(PosterImage::getImageUrl)
                                .collect(Collectors.toList()),
                        poster.getInstitution(),
                        poster.getEvent().getEventType(),
                        poster.getEvent().getStartDate(),
                        poster.getEvent().getEndDate(),
                        poster.getEvent().getAddress(),
                        poster.getEvent().getTicketName(),
                        poster.getEvent().getTicketPrice(),
                        poster.getEvent().getDescription(),
                        poster.getEvent().getMood()
                ));
    }

    public PosterDto getPoster(Long posterId, Status status) {
        // posterId로 해당 Poster 찾아오기
        Poster poster = posterRepository.findByStatusAndPosterId(status, posterId).orElseThrow(
                () -> new IllegalArgumentException(ResponseMessage.POSTER_INVALID_POSTER.getMessage())
        );

        // 찾은 Poster를 PosterDto에 담기
        PosterDto posterDto = new PosterDto(poster.getPosterId(),
                poster.getTitle(),
                poster.getPosterImages().stream()
                        .map(PosterImage::getImageUrl)
                        .collect(Collectors.toList()),
                poster.getInstitution(),
                poster.getEvent().getEventType(),
                poster.getEvent().getStartDate(),
                poster.getEvent().getEndDate(),
                poster.getEvent().getAddress(),
                poster.getEvent().getTicketName(),
                poster.getEvent().getTicketPrice(),
                poster.getEvent().getDescription(),
                poster.getEvent().getMood());

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
                .map(poster -> new PosterDto(
                        poster.getPosterId(),
                        poster.getTitle(),
                        poster.getPosterImages().stream()
                                .map(PosterImage::getImageUrl)
                                .collect(Collectors.toList()),
                        poster.getInstitution(),
                        poster.getEvent().getEventType(),
                        poster.getEvent().getStartDate(),
                        poster.getEvent().getEndDate(),
                        poster.getEvent().getAddress(),
                        poster.getEvent().getTicketName(),
                        poster.getEvent().getTicketPrice(),
                        poster.getEvent().getDescription(),
                        poster.getEvent().getMood()
                ));
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
            }
        } else {  // 관심 해제
            if (find.isPresent()) {
                PosterLike posterLike = find.get();
                posterLikeRepository.delete(posterLike);
            }
        }
    }
}
