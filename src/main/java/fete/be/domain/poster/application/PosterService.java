package fete.be.domain.poster.application;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fete.be.domain.admin.application.dto.request.SetArtistImageUrlsRequest;
import fete.be.domain.admin.application.dto.response.AccountDto;
import fete.be.domain.admin.application.dto.response.SimplePosterDto;
import fete.be.domain.event.persistence.*;
import fete.be.domain.image.application.ImageUploadService;
import fete.be.domain.member.application.MemberService;
import fete.be.domain.member.persistence.Member;
import fete.be.domain.admin.application.dto.request.ApprovePostersRequest;
import fete.be.domain.member.persistence.Role;
import fete.be.domain.poster.application.dto.request.Filter;
import fete.be.domain.poster.application.dto.request.ModifyPosterRequest;
import fete.be.domain.poster.application.dto.request.WritePosterRequest;
import fete.be.domain.poster.application.dto.response.PosterDto;
import fete.be.domain.poster.exception.NotFoundPosterException;
import fete.be.domain.poster.exception.ProfileImageCountExceedException;
import fete.be.domain.poster.exception.ProfileImageCountMismatchException;
import fete.be.domain.poster.persistence.*;
import fete.be.global.util.ResponseMessage;
import fete.be.global.util.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class PosterService {

    private final JPAQueryFactory queryFactory;
    private final PosterRepository posterRepository;
    private final EventRepository eventRepository;
    private final PosterLikeRepository posterLikeRepository;
    private final MemberService memberService;
    private final ImageUploadService imageUploadService;


    @Transactional
    public Long writePoster(WritePosterRequest request) {
        // 현재 요청한 Member의 email을 추출해서 member 찾아오기
        Member member = memberService.findMemberByEmail();

        // 포스터 이미지 개수가 10개를 초과할 경우
        if (request.getPosterImgUrls().length > 10) {
            throw new ProfileImageCountExceedException(ResponseMessage.POSTER_IMAGE_COUNT_EXCEED.getMessage());
        }

        Poster poster = Poster.createPoster(member, request);
        Poster savedPoster = posterRepository.save(poster);

        Event event = Event.createEvent(savedPoster, request.getEvent());
        Event savedEvent = eventRepository.save(event);

        Poster result = Poster.setEvent(savedPoster, savedEvent);

        return result.getPosterId();
    }

    public Poster findPosterByPosterId(Long posterId) {
        return posterRepository.findById(posterId).orElseThrow(
                () -> new NotFoundPosterException(ResponseMessage.POSTER_NO_EXIST.getMessage()));
    }

    @Transactional
    public Long updatePoster(Long posterId, ModifyPosterRequest request) throws URISyntaxException {
        // 현재 API 요청을 보낸 Member 찾기
        Member member = memberService.findMemberByEmail();

        // posterId로 포스터 찾기
        Poster poster = findPosterByPosterId(posterId);

        // 수정을 요청한 사용자가 해당 포스터의 작성자가 아닌 경우 (관리자는 가능)
        if (!poster.getMember().equals(member)) {
            if (!member.getRole().equals(Role.ADMIN)) {
                throw new IllegalArgumentException(ResponseMessage.POSTER_INVALID_USER.getMessage());
            }
        }

        // 업데이트 진행
        Poster updatePoster = Poster.updatePoster(poster, request, imageUploadService);
        posterRepository.save(updatePoster);

        return updatePoster.getPosterId();
    }

    @Transactional
    public Long deletePoster(Long posterId) throws URISyntaxException {
        // 현재 API 요청을 보낸 Member 찾기
        Member member = memberService.findMemberByEmail();

        // posterId로 포스터 찾기
        Poster poster = findPosterByPosterId(posterId);

        // 삭제를 요청한 사용자가 해당 포스터의 작성자가 아닌 경우
        if (!poster.getMember().equals(member)) {
            throw new IllegalArgumentException(ResponseMessage.POSTER_INVALID_USER.getMessage());
        }

        // 소프트 삭제 실행 (status 필드를 DELETE로 변경)
        Poster deletePoster = Poster.deletePoster(poster, imageUploadService);
        return deletePoster.getPosterId();
    }

    public Page<PosterDto> getPosters(Status status, int page, int size) {
        // 페이징 조건 추가
        Pageable pageable = createPageable(page, size);

        // Member 정보
        Member member = memberService.findMemberByEmail();

        // 조건에 맞는 Poster 가져오기
        return posterRepository.findByStatus(status, pageable)
                .map(poster -> {
                    Boolean isLike = posterLikeRepository.findByMemberIdAndPosterId(member.getMemberId(), poster.getPosterId()).isPresent();
                    return new PosterDto(poster, isLike);
                });
    }

    public Page<PosterDto> getGuestPosters(Status status, int page, int size) {
        // 페이징 조건 추가
        Pageable pageable = createPageable(page, size);

        return posterRepository.findByStatus(status, pageable)
                .map(poster -> {
                    Boolean isLike = false;
                    return new PosterDto(poster, isLike);
                });
    }

    // 다중 필터링 포스터 조회
    public Page<PosterDto> getPostersWithFilters(int page, int size, Filter filter) {
        // 페이징 조건 추가
        Pageable pageable = createPageable(page, size);

        // Member 정보
        Member member = memberService.findMemberByEmail();

        // 사용할 QClass
        QPoster poster = QPoster.poster;
        QEvent event = QEvent.event;

        // 동적 쿼리
        BooleanBuilder builder = new BooleanBuilder();

        // 포스터 상태 필터 적용 (필수)
        builder.and(poster.status.eq(Status.valueOf(filter.getStatus())));

        // 필터링 조건 추가 (선택)
        if (filter.getSimpleAddress() != null) {
            builder.and(event.simpleAddress.contains(filter.getSimpleAddress()));
        }
        if (filter.getMood() != null) {
            builder.and(poster.event.mood.eq(Mood.convertMoodEnum(filter.getMood())));
        }
        if (filter.getGenre() != null) {
            builder.and(poster.event.genre.eq(Genre.convertGenreEnum(filter.getGenre())));
        }
        if (filter.getMinPrice() != null && filter.getMaxPrice() != null) {
            builder.and(poster.event.tickets.any().ticketPrice.between(filter.getMinPrice(), filter.getMaxPrice()));
        }
        if (filter.getStartDate() != null && filter.getEndDate() != null) {
            builder.and(event.startDate.goe(filter.getStartDate()).and(event.endDate.loe(filter.getEndDate())));
        }

        // 최종 조회 쿼리 실행
        List<Poster> result = queryFactory.selectFrom(poster)
                .join(poster.event, event)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 관심 등록 상태를 반영하여 PosterDto에 담아 반환
        return new PageImpl<>(result.stream()
                .map(posterItem -> {
                    Boolean isLike = posterLikeRepository.findByMemberIdAndPosterId(member.getMemberId(), posterItem.getPosterId()).isPresent();
                    return new PosterDto(posterItem, isLike);
                })
                .collect(Collectors.toList()), pageable, result.size());
    }

    // 게스트용 필터링 포스터 조회 메서드
    public Page<PosterDto> getGuestPostersWithFilters(int page, int size, Filter filter) {
        // 페이징 조건 추가
        Pageable pageable = createPageable(page, size);

        // 사용할 QClass
        QPoster poster = QPoster.poster;
        QEvent event = QEvent.event;

        // 동적 쿼리
        BooleanBuilder builder = new BooleanBuilder();

        // 포스터 상태 필터 적용 (필수)
        builder.and(poster.status.eq(Status.valueOf(filter.getStatus())));

        // 필터링 조건 추가 (선택)
        if (filter.getSimpleAddress() != null) {
            builder.and(event.simpleAddress.contains(filter.getSimpleAddress()));
        }
        if (filter.getMood() != null) {
            builder.and(poster.event.mood.eq(Mood.convertMoodEnum(filter.getMood())));
        }
        if (filter.getGenre() != null) {
            builder.and(poster.event.genre.eq(Genre.convertGenreEnum(filter.getGenre())));
        }
        if (filter.getMinPrice() != null && filter.getMaxPrice() != null) {
            builder.and(poster.event.tickets.any().ticketPrice.between(filter.getMinPrice(), filter.getMaxPrice()));
        }
        if (filter.getStartDate() != null && filter.getEndDate() != null) {
            builder.and(event.startDate.goe(filter.getStartDate()).and(event.endDate.loe(filter.getEndDate())));
        }

        // 최종 조회 쿼리 실행
        List<Poster> result = queryFactory.selectFrom(poster)
                .join(poster.event, event)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // PosterDto에 담아 반환
        return new PageImpl<>(result.stream()
                .map(posterItem -> {
                    Boolean isLike = false;
                    return new PosterDto(posterItem, isLike);
                })
                .collect(Collectors.toList()), pageable, result.size());
    }

    public PosterDto getPoster(Long posterId, Status status) {
        // 유저 정보
        Member member = memberService.findMemberByEmail();

        // posterId로 해당 Poster 찾아오기
        Poster poster = posterRepository.findByStatusAndPosterId(status, posterId).orElseThrow(
                () -> new NotFoundPosterException(ResponseMessage.POSTER_NO_EXIST.getMessage())
        );

        // 관심 등록 상태
        Boolean isLike = posterLikeRepository.findByMemberIdAndPosterId(member.getMemberId(), poster.getPosterId()).isPresent();

        // 이벤트 등록자라면, 계좌 정보를 포함하여 반환
        AccountDto account;
        if (member.equals(poster.getMember())) {
            account = new AccountDto(poster.getEvent());
            return new PosterDto(poster, isLike, account);
        }

        // 찾은 Poster를 PosterDto에 담아 반환
        return new PosterDto(poster, isLike);
    }

    public PosterDto getGuestPoster(Long posterId, Status status) {
        // posterId로 해당 Poster 찾아오기
        Poster poster = posterRepository.findByStatusAndPosterId(status, posterId).orElseThrow(
                () -> new NotFoundPosterException(ResponseMessage.POSTER_NO_EXIST.getMessage())
        );

        // 관심 등록 상태
        Boolean isLike = false;

        // 찾은 Poster를 PosterDto에 담아 반환
        return new PosterDto(poster, isLike);
    }

    @Transactional
    public void approvePosters(ApprovePostersRequest request) {
        List<Long> posterIds = request.getPosterIds();
        List<Poster> posters = posterRepository.findAllById(posterIds);  // posterIds로 한번에 대량 조회

        // 승인 요청한 포스터의 수와 찾은 포스터의 수가 일치하지 않는 경우
        if (posters.size() != posterIds.size()) {
            throw new NotFoundPosterException(ResponseMessage.POSTER_NO_EXIST.getMessage());
        }

        for (Poster poster : posters) {
            Poster.approvePoster(poster);  // 관리자 승인 메서드 실행
        }
    }

    public Page<PosterDto> getMyPosters(int page, int size) {
        // 페이징 조건 추가
        Pageable pageable = createPageable(page, size);

        // Member 찾기
        Member member = memberService.findMemberByEmail();

        // 조건에 맞는 데이터 조회
        return posterRepository.findByMemberAndStatusNot(member, Status.DELETE, pageable)
                .map(poster -> {
                    Boolean isLike = posterLikeRepository.findByMemberIdAndPosterId(member.getMemberId(), poster.getPosterId()).isPresent();
                    return new PosterDto(poster, isLike);
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
        Pageable pageable = createPageable(page, size);

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
                .map(poster -> {
                    Boolean isLike = true;
                    return new PosterDto(poster, isLike);
                });
    }

    public Page<PosterDto> searchPosters(String keyword, int page, int size) {
        // 페이징 조건 추가
        Pageable pageable = createPageable(page, size);

        // Member 찾기
        Member member = memberService.findMemberByEmail();

        // 포스터의 제목 또는 이벤트 설명에 해당 키워드가 포함되어 있는 포스터들만 조회
        return posterRepository.findByKeyword(keyword, pageable)
                .map(poster -> {
                    Boolean isLike = posterLikeRepository.findByMemberIdAndPosterId(member.getMemberId(), poster.getPosterId()).isPresent();
                    return new PosterDto(poster, isLike);
                });
    }

    public Page<PosterDto> searchGuestPosters(String keyword, int page, int size) {
        // 페이징 조건 추가
        Pageable pageable = createPageable(page, size);

        // 포스터의 제목 또는 이벤트 설명에 해당 키워드가 포함되어 있는 포스터들만 조회
        return posterRepository.findByKeyword(keyword, pageable)
                .map(poster -> {
                    Boolean isLike = false;
                    return new PosterDto(poster, isLike);
                });
    }

    // 매일 정오마다 실행
    @Scheduled(cron = "0 0 12 * * ?")
    @Transactional
    public void checkEndedPoster() {
        // 종료된 포스터 조회
        List<Poster> endedPosters = posterRepository.findEndDateBeforeNow(LocalDateTime.now());

        // 종료된 포스터의 status를 END로 변경
        for (Poster poster : endedPosters) {
            Poster.endPoster(poster);
        }
    }

    // 관리자용 간편 포스터 전체 조회
    public Page<SimplePosterDto> getSimplePosters(Status status, int page, int size) {
        // 페이징 조건 추가
        Pageable pageable = createPageable(page, size);

        // 조건에 맞는 Poster 가져오기
        return posterRepository.findByStatus(status, pageable)
                .map(poster -> {
                    Boolean isLike = false;
                    return new SimplePosterDto(poster, isLike);
                });
    }

    // 관리자용 아티스트 프로필 이미지 등록
    @Transactional
    public void setArtistImageUrls(Long posterId, SetArtistImageUrlsRequest request) {
        // 수정할 포스터 조회
        Poster poster = findPosterByPosterId(posterId);
        Event event = poster.getEvent();
        List<Artist> artists = event.getArtists();

        // 등록할 아티스트 프로필 이미지 리스트
        List<String> imageUrls = request.getImageUrls();

        // 아티스트 수와 전달된 프로필 이미지 수가 다르다면 예외 처리
        if (artists.size() != imageUrls.size()) {
            throw new ProfileImageCountMismatchException(ResponseMessage.ADMIN_INVALID_ARTIST_PROFILE_COUNT.getMessage());
        }

        // 아티스트 이미지 등록
        Artist.updateInfoUrls(artists, imageUrls);
    }

    // 관리자용 간단 주소 변경
    @Transactional
    public void modifySimpleAddress(Long posterId, String simpleAddress) {
        // 수정할 포스터 조회
        Poster poster = findPosterByPosterId(posterId);
        Event event = poster.getEvent();

        // 간단 주소 변경
        event.updateSimpleAddress(simpleAddress);
    }

    /**
     * Pageable 객체 생성
     * - 첫 번째 정렬 기준 : 이벤트의 시작 날짜
     * - 두 번째 정렬 기준 : 이벤트 이름
     */
    private Pageable createPageable(int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Order.asc("event.startDate"),  // 첫 번째 정렬 기준: 이벤트 시작 날짜
                        Sort.Order.asc("event.eventName")  // 두 번째 정렬 기준: 이벤트 이름
                )
        );
        return pageable;
    }
}
