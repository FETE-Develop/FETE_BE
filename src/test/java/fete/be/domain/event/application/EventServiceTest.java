package fete.be.domain.event.application;

import fete.be.domain.admin.application.dto.request.ApprovePostersRequest;
import fete.be.domain.admin.application.dto.response.AccountDto;
import fete.be.domain.event.application.dto.request.BuyTicketDto;
import fete.be.domain.event.application.dto.request.BuyTicketRequest;
import fete.be.domain.event.exception.IncorrectTicketPriceException;
import fete.be.domain.event.exception.IncorrectTicketTypeException;
import fete.be.domain.event.exception.InsufficientTicketsException;
import fete.be.domain.event.persistence.ArtistDto;
import fete.be.domain.event.persistence.TicketInfoDto;
import fete.be.domain.member.application.MemberService;
import fete.be.domain.member.application.dto.request.SignupRequestDto;
import fete.be.domain.member.persistence.Gender;
import fete.be.domain.poster.application.PosterService;
import fete.be.domain.poster.application.dto.request.EventDto;
import fete.be.domain.poster.application.dto.request.Place;
import fete.be.domain.poster.application.dto.request.WritePosterRequest;
import fete.be.domain.poster.persistence.Poster;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class EventServiceTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private PosterService posterService;
    @Autowired
    private EventService eventService;

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
    @DisplayName("티켓 결제 시스템")
    class BuyTicket {

        @DisplayName("무료 티켓 구매")
        @Test
        void 성공_무료티켓_구매() throws Exception {
            // given - 포스터 등록 후, 승인
            WritePosterRequest writePosterRequest = generateWriteRequest();
            Long posterId = posterService.writePoster(writePosterRequest);
            ApprovePostersRequest request = new ApprovePostersRequest(List.of(posterId));
            posterService.approvePosters(request);
            Poster approvedPoster = posterService.findPosterByPosterId(posterId);

            // when - 무료티켓 3장 구매
            BuyTicketRequest buyTicketRequest = new BuyTicketRequest(
                    List.of(new BuyTicketDto("현장구매", 0, 3)),
                    null
            );
            List<String> qrCodes = eventService.buyTicket(approvedPoster.getPosterId(), buyTicketRequest);

            // then - QR 코드가 3개 발급되어야 한다.
            assertThat(qrCodes.size()).isEqualTo(3);
        }

        @DisplayName("티켓의 수량이 부족한 경우")
        @Test
        void 예외사항_티켓수량_부족() {
            // given - 포스터 등록 후, 승인
            WritePosterRequest writePosterRequest = generateWriteRequest();
            Long posterId = posterService.writePoster(writePosterRequest);
            ApprovePostersRequest request = new ApprovePostersRequest(List.of(posterId));
            posterService.approvePosters(request);
            Poster approvedPoster = posterService.findPosterByPosterId(posterId);

            // when & then - 현재 남아있는 티켓의 수량을 초과하여 구매 요청할 경우
            BuyTicketRequest buyTicketRequest = new BuyTicketRequest(
                    List.of(new BuyTicketDto("현장구매", 0, 16)),
                    null
            );

            assertThrows(InsufficientTicketsException.class, () -> {
                List<String> qrCodes = eventService.buyTicket(approvedPoster.getPosterId(), buyTicketRequest);
            });
        }

        @DisplayName("티켓의 가격이 올바르지 않은 경우")
        @Test
        void 예외사항_티켓가격_불일치() {
            // given - 포스터 등록 후, 승인
            WritePosterRequest writePosterRequest = generateWriteRequest();
            Long posterId = posterService.writePoster(writePosterRequest);
            ApprovePostersRequest request = new ApprovePostersRequest(List.of(posterId));
            posterService.approvePosters(request);
            Poster approvedPoster = posterService.findPosterByPosterId(posterId);

            // when & then - 결제 요청된 가격과 DB의 가격이 불일치 하는 경우
            BuyTicketRequest buyTicketRequest = new BuyTicketRequest(
                    List.of(new BuyTicketDto("현장구매", 1000, 5)),
                    null
            );

            assertThrows(IncorrectTicketPriceException.class, () -> {
                List<String> qrCodes = eventService.buyTicket(approvedPoster.getPosterId(), buyTicketRequest);
            });
        }

        @DisplayName("티켓의 종류가 올바르지 않은 경우")
        @Test
        void 예외사항_티켓종류_불일치() {
            // given - 포스터 등록 후, 승인
            WritePosterRequest writePosterRequest = generateWriteRequest();
            Long posterId = posterService.writePoster(writePosterRequest);
            ApprovePostersRequest request = new ApprovePostersRequest(List.of(posterId));
            posterService.approvePosters(request);
            Poster approvedPoster = posterService.findPosterByPosterId(posterId);

            // when & then - 결제 요청된 티켓 종류와 DB의 티켓 종류가 불일치 하는 경우
            BuyTicketRequest buyTicketRequest = new BuyTicketRequest(
                    List.of(new BuyTicketDto("프로모션", 0, 5)),
                    null
            );

            assertThrows(IncorrectTicketTypeException.class, () -> {
                List<String> qrCodes = eventService.buyTicket(approvedPoster.getPosterId(), buyTicketRequest);
            });
        }

        @DisplayName("티켓 구매 후, 수량 업데이트")
        @Test
        void 티켓구매후_수량_업데이트() throws Exception {
            // given - 포스터 등록 후, 승인
            WritePosterRequest writePosterRequest = generateWriteRequest();
            Long posterId = posterService.writePoster(writePosterRequest);
            ApprovePostersRequest request = new ApprovePostersRequest(List.of(posterId));
            posterService.approvePosters(request);
            Poster approvedPoster = posterService.findPosterByPosterId(posterId);

            // when - 티켓 5장 구매
            int ticketNumber = 5;
            BuyTicketRequest buyTicketRequest = new BuyTicketRequest(
                    List.of(new BuyTicketDto("현장구매", 0, ticketNumber)),
                    null
            );
            List<String> qrCodes = eventService.buyTicket(approvedPoster.getPosterId(), buyTicketRequest);

            // then - 판매된 티켓이 5장이어야 한다.
            Poster after = posterService.findPosterByPosterId(posterId);
            assertThat(after.getEvent().getTickets().get(1).getSoldTicketCount()).isEqualTo(ticketNumber);
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
                new TicketInfoDto("현장구매", "현장구매는 현장에서 구매하셔야 합니다.", 0, 15)
        );

        // 아티스트 정보 생성
        List<ArtistDto> artists = Arrays.asList(
                new ArtistDto("이하이", "https://info.url", ""),
                new ArtistDto("악동뮤지션", "https://info.url", "")
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
}