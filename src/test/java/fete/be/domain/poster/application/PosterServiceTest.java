package fete.be.domain.poster.application;

import fete.be.domain.admin.application.dto.request.ApprovePostersRequest;
import fete.be.domain.admin.application.dto.request.RejectPosterRequest;
import fete.be.domain.admin.application.dto.response.AccountDto;
import fete.be.domain.event.persistence.ArtistDto;
import fete.be.domain.event.persistence.Genre;
import fete.be.domain.event.persistence.TicketInfoDto;
import fete.be.domain.member.application.MemberService;
import fete.be.domain.member.application.dto.request.SignupRequestDto;
import fete.be.domain.member.persistence.Gender;
import fete.be.domain.poster.application.dto.request.*;
import fete.be.domain.poster.application.dto.response.PosterDto;
import fete.be.domain.poster.persistence.Poster;
import fete.be.global.util.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class PosterServiceTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private PosterService posterService;


    @BeforeEach
    void setUp() {
        // SecurityContext에 인증정보 임의로 설정
        String tempEmail = "kky6335@gmail.com";
        String tempPassword = "hawon1234!";

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(tempEmail, tempPassword, List.of());
        SecurityContext securityContext = new SecurityContextImpl(authentication);
        SecurityContextHolder.setContext(securityContext);

        // 계정 생성
        SignupRequestDto signupRequestDto = new SignupRequestDto("kky6335@gmail.com", "hawon1234!",
                "www.profile.image.com", "강건영", "강건영입니다.", "2000-12-31", Gender.MALE, "01012345678");
        memberService.signUp(signupRequestDto);
    }

    @Nested
    @DisplayName("포스터 CRUD")
    class Poster_CRUD {

        @DisplayName("포스터 등록")
        @Test
        void 성공_포스터_등록() {
            // given
            WritePosterRequest writePosterRequest = generateWriteRequest();

            // when
            Long posterId = posterService.writePoster(writePosterRequest);
            Poster findPoster = posterService.findPosterByPosterId(posterId);

            // then
            assertThat(posterId).isEqualTo(findPoster.getPosterId());
            assertThat(findPoster.getMember().getUserName()).isEqualTo("강건영");
        }

        @DisplayName("포스터 수정 (eventName, manager 수정)")
        @Test
        void 성공_포스터_수정() throws URISyntaxException {
            // given
            WritePosterRequest writePosterRequest = generateWriteRequest();
            Long posterId = posterService.writePoster(writePosterRequest);
            Poster originPoster = posterService.findPosterByPosterId(posterId);
            String originManger = originPoster.getManager();
            String originEventName = originPoster.getEvent().getEventName();

            // when - eventName, manager 수정했을 경우
            ModifyPosterRequest modifyPosterRequest = generateModifyRequest();
            Long modifiedPosterId = posterService.updatePoster(posterId, modifyPosterRequest);
            Poster modifiedPoster = posterService.findPosterByPosterId(modifiedPosterId);

            // then
            assertThat(posterId).isEqualTo(modifiedPosterId);
            assertThat(modifiedPoster.getManager()).isNotEqualTo(originManger);
            assertThat(modifiedPoster.getEvent().getEventName()).isNotEqualTo(originEventName);
        }

        @DisplayName("포스터 삭제 (소프트 삭제)")
        @Test
        void 성공_포스터_삭제() throws URISyntaxException {
            // given
            WritePosterRequest writePosterRequest = generateDeleteRequest();
            Long posterId = posterService.writePoster(writePosterRequest);

            // when - 해당 포스터 삭제 (소프트 삭제)
            Long deletedPosterId = posterService.deletePoster(posterId);
            Poster deletedPoster = posterService.findPosterByPosterId(deletedPosterId);

            // then
            assertThat(deletedPoster.getStatus()).isEqualTo(Status.DELETE);
        }

        @Nested
        @DisplayName("포스터 조회")
        class GetPoster {

            @DisplayName("포스터 전체 조회")
            @Test
            void 성공_포스터_전체_조회() {
                // given - 포스터 1개 등록했을 경우
                WritePosterRequest writePosterRequest = generateWriteRequest();
                Long posterId = posterService.writePoster(writePosterRequest);

                // when
                List<PosterDto> posters = posterService.getPosters(Status.WAIT, 0, 10).getContent();

                // then
                assertThat(posters.size()).isEqualTo(1);
            }

            @DisplayName("포스터 필터링 조회")
            @Test
            void 성공_포스터_필터링_조회() {
                // given - 포스터 1개 등록했을 경우
                WritePosterRequest writePosterRequest = generateWriteRequest();
                Long posterId = posterService.writePoster(writePosterRequest);

                // when - 원하는 필터만 사용 가능
                Filter filter = new Filter("WAIT", null, "몽환적인", "DISCO", null, null, null, null);
                List<PosterDto> posters = posterService.getPostersWithFilters(0, 10, filter).getContent();

                // then
                assertThat(posters.get(0).getMoods()).contains("몽환적인");
                assertThat(posters.get(0).getGenres()).contains("DISCO");
                assertThat(posters.size()).isEqualTo(1);
            }

            @DisplayName("포스터 단건 조회")
            @Test
            void 성공_포스터_단건_조회() {
                // given - 포스터 1개 등록했을 경우
                WritePosterRequest writePosterRequest = generateWriteRequest();
                Long posterId = posterService.writePoster(writePosterRequest);
                Status wantedStatus = Status.WAIT;

                // when
                PosterDto poster = posterService.getPoster(posterId, wantedStatus);

                // then
                assertThat(poster.getPosterId()).isEqualTo(posterId);
                assertThat(poster.getStatus()).isEqualTo(wantedStatus);
                assertThat(poster.getGenres()).contains(Genre.D_AND_B.getKoreanValue());
            }
        }
    }

    @DisplayName("포스터 승인")
    @Test
    void 성공_포스터_승인() {
        // given - 포스터 등록 (WAIT 상태)
        WritePosterRequest writePosterRequest = generateWriteRequest();
        Long posterId = posterService.writePoster(writePosterRequest);

        // when - 등록한 포스터 승인 (ACTIVE로 변경)
        ApprovePostersRequest request = new ApprovePostersRequest(List.of(posterId));
        posterService.approvePosters(request);
        Poster approvedPoster = posterService.findPosterByPosterId(posterId);

        // then
        assertThat(approvedPoster.getStatus()).isEqualTo(Status.ACTIVE);
    }

    @DisplayName("포스터 반려")
    @Test
    void 성공_포스터_반려() {
        // given - 포스터 등록 (WAIT 상태)
        WritePosterRequest writePosterRequest = generateWriteRequest();
        Long posterId = posterService.writePoster(writePosterRequest);

        // when - 등록한 포스터 반려 (REJECT로 변경)
        RejectPosterRequest request = new RejectPosterRequest(posterId, "반려 사유입니다.");
        posterService.rejectPoster(request);
        Poster rejectedPoster = posterService.findPosterByPosterId(posterId);

        // then
        assertThat(rejectedPoster.getStatus()).isEqualTo(Status.REJECT);
    }

    @DisplayName("포스터 관심 등록")
    @Test
    void 성공_포스터_관심등록() {
        // given - 포스터 등록
        WritePosterRequest writePosterRequest = generateWriteRequest();
        Long posterId = posterService.writePoster(writePosterRequest);

        // when - 등록한 포스터 관심 등록
        posterService.likePoster(posterId, true);
        List<PosterDto> likedPosters = posterService.getLikePosters(0, 10).getContent();

        // then
        assertThat(likedPosters.size()).isEqualTo(1);
    }

    @Nested
    @DisplayName("포스터 검색")
    class Search {

        @DisplayName("포스터 검색")
        @Test
        void 성공_포스터_검색() {
            // given - 포스터 등록 후, 승인
            WritePosterRequest writePosterRequest = generateWriteRequest();
            Long posterId = posterService.writePoster(writePosterRequest);
            ApprovePostersRequest request = new ApprovePostersRequest(List.of(posterId));
            posterService.approvePosters(request);

            // when - "FETE" 키워드로 검색
            String keyword = "FETE";
            List<PosterDto> searchedPosters = posterService.searchPosters(keyword, 0, 10).getContent();

            // then
            assertThat(searchedPosters.size()).isEqualTo(1);
            assertThat(searchedPosters.get(0).getEventName()).contains(keyword);
        }

        @DisplayName("포스터 검색 (검색 결과 없을 때)")
        @Test
        void 성공_포스터_검색_결과없음() {
            // given - 포스터 등록 후, 승인
            WritePosterRequest writePosterRequest = generateWriteRequest();
            Long posterId = posterService.writePoster(writePosterRequest);
            ApprovePostersRequest request = new ApprovePostersRequest(List.of(posterId));
            posterService.approvePosters(request);

            // when - "FETE" 키워드로 검색
            String keyword = "DJ";
            List<PosterDto> searchedPosters = posterService.searchPosters(keyword, 0, 10).getContent();

            // then
            assertThat(searchedPosters.size()).isEqualTo(0);
        }
    }


    /**
     * 포스터 등록 DTO 객체 생성
     *
     * @return WritePosterRequest
     */
    WritePosterRequest generateWriteRequest() {
        // 장소 정보 생성
        Place place = new Place(
                "경기도 화성시 병점중앙로16 101동 106호",
                "병점동",
                "2층",
                41.01234,
                39.1234
        );

        // 티켓 정보 생성
        List<TicketInfoDto> tickets = Arrays.asList(
                new TicketInfoDto("얼리버드", "얼리버드는 티켓의 간단한 소개 내용입니다.", 10000, 10),
                new TicketInfoDto("현장구매", "현장구매는 현장에서 구매하셔야 합니다.", 0, 8)
        );

        // 아티스트 정보 생성
        List<ArtistDto> artists = Arrays.asList(
                new ArtistDto("BTS", "https://info.url", ""),
                new ArtistDto("아이유", "https://info.url", "")
        );

        // 계좌 정보 생성
        AccountDto account = new AccountDto(
                "농협",
                "20708012341234",
                "강건영"
        );

        // 이벤트 정보 생성
        EventDto event = new EventDto(
                "FETE의 첫 이벤트",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(3),
                place,
                false,
                false,
                false,
                tickets,
                "이벤트 관련 추가 설명입니다",
                "몽환적인,잔잔한",
                "POP,DISCO,D&B",
                "http://www.instargram.kr",
                artists,
                account
        );

        // 포스터 등록 DTO 생성
        WritePosterRequest request = new WritePosterRequest(
                new String[]{
                        "https://picsum.photos/230/530",
                        "https://picsum.photos/300/500",
                        "https://picsum.photos/1080/2080",
                        "https://picsum.photos/1080/400"
                },
                "FETE",
                "강건영",
                "010-1234-5678",
                event
        );

        return request;
    }

    /**
     * 포스터 수정 DTO 객체 생성
     *
     * @return ModifyPosterRequest
     */
    ModifyPosterRequest generateModifyRequest() {
        // 장소 정보 생성
        Place place = new Place(
                "경기도 화성시 병점중앙로16 101동 106호",
                "병점동",
                "2층",
                41.01234,
                39.1234
        );

        // 아티스트 정보 생성
        List<ArtistDto> artists = Arrays.asList(
                new ArtistDto("BTS", "https://info.url", ""),
                new ArtistDto("아이유", "https://info.url", "")
        );

        // 계좌 정보 생성
        AccountDto account = new AccountDto(
                "농협",
                "20708012341234",
                "강건영"
        );

        // 이벤트 정보 생성
        EventDto event = new EventDto(
                "FETE의 두 번째 이벤트",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(3),
                place,
                false,
                false,
                false,
                null,
                "이벤트 관련 추가 설명입니다",
                "잔잔한",
                "DISCO",
                "http://www.instargram.kr",
                artists,
                account
        );

        // 포스터 수정 DTO 생성
        ModifyPosterRequest request = new ModifyPosterRequest(
                new String[]{
                        "https://picsum.photos/230/530",
                        "https://picsum.photos/300/500",
                        "https://picsum.photos/1080/2080",
                        "https://picsum.photos/1080/400"
                },
                "FETE",
                "하원",
                "010-1234-5678",
                event
        );

        return request;
    }

    /**
     * 포스터 삭제용 DTO 객체 생성
     * - 삭제 API에서는 S3에서 이미지를 삭제하는 로직이 있어서, 이미지 없이 포스터 등록
     *
     * @return WritePosterRequest
     */
    WritePosterRequest generateDeleteRequest() {
        // 장소 정보 생성
        Place place = new Place(
                "경기도 화성시 병점중앙로16 101동 106호",
                "병점동",
                "2층",
                41.01234,
                39.1234
        );

        // 티켓 정보 생성
        List<TicketInfoDto> tickets = Arrays.asList(
                new TicketInfoDto("얼리버드", "얼리버드는 티켓의 간단한 소개 내용입니다.", 10000, 10),
                new TicketInfoDto("현장구매", "현장구매는 현장에서 구매하셔야 합니다.", 0, 8)
        );

        // 아티스트 정보 생성
        List<ArtistDto> artists = Arrays.asList(
                new ArtistDto("BTS", "https://info.url", ""),
                new ArtistDto("아이유", "https://info.url", "")
        );

        // 계좌 정보 생성
        AccountDto account = new AccountDto(
                "농협",
                "20708012341234",
                "강건영"
        );

        // 이벤트 정보 생성
        EventDto event = new EventDto(
                "FETE의 첫 이벤트",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(3),
                place,
                false,
                false,
                false,
                tickets,
                "이벤트 관련 추가 설명입니다",
                "몽환적인,잔잔한",
                "POP,DISCO",
                "http://www.instargram.kr",
                artists,
                account
        );

        // 포스터 등록 DTO 생성
        WritePosterRequest request = new WritePosterRequest(
                new String[]{
                        ""
                },
                "FETE",
                "강건영",
                "010-1234-5678",
                event
        );

        return request;
    }
}