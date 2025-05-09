package fete.be.domain.event.application;

import fete.be.domain.admin.application.dto.request.ApprovePostersRequest;
import fete.be.domain.admin.application.dto.response.AccountDto;
import fete.be.domain.event.application.dto.request.BuyTicketDto;
import fete.be.domain.event.application.dto.request.BuyTicketRequest;
import fete.be.domain.event.application.dto.request.ParticipantDto;
import fete.be.domain.event.persistence.ArtistDto;
import fete.be.domain.event.persistence.TicketInfoDto;
import fete.be.domain.member.application.MemberService;
import fete.be.domain.member.application.dto.request.SignupRequestDto;
import fete.be.domain.member.persistence.Gender;
import fete.be.domain.member.persistence.Member;
import fete.be.domain.poster.application.PosterService;
import fete.be.domain.poster.application.dto.request.EventDto;
import fete.be.domain.poster.application.dto.request.Place;
import fete.be.domain.poster.application.dto.request.WritePosterRequest;
import fete.be.domain.poster.persistence.Poster;
import fete.be.domain.ticket.persistence.Participant;
import fete.be.domain.ticket.persistence.ParticipantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class QRCodeServiceTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private PosterService posterService;
    @Autowired
    private EventService eventService;
    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private ParticipantRepository participantRepository;

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

    @DisplayName("QR 코드 티켓 인증")
    @Test
    void 성공_QR코드_티켓_인증() throws Exception {
        // given #1 - 포스터 등록 후, 승인
        WritePosterRequest writePosterRequest = generateWriteRequest();
        Long posterId = posterService.writePoster(writePosterRequest);
        ApprovePostersRequest request = new ApprovePostersRequest(List.of(posterId));
        posterService.approvePosters(request);
        Poster approvedPoster = posterService.findPosterByPosterId(posterId);

        // given #2 - 무료 티켓 3장 구매
        BuyTicketRequest buyTicketRequest = new BuyTicketRequest(
                List.of(new BuyTicketDto("현장구매", 0, 3)),
                null
        );
        List<String> qrCodes = eventService.buyTicket(approvedPoster.getPosterId(), buyTicketRequest);

        // given #3 - 구입한 티켓 내역 조회
        Member member = memberService.findMemberByEmail();
        List<Participant> participants = participantRepository.findByMember(member);
        Participant participant = participants.get(0);

        // when - 발급된 QR 코드 인증 실행
        ParticipantDto participantDto = new ParticipantDto(participant.getParticipantId(), member.getMemberId(),
                approvedPoster.getEvent().getEventId(), participant.getPayment().getPaymentId());
        Long usedParticipantId = qrCodeService.verifyQRCode(posterId, participantDto);
        Optional<Participant> usedParticipant = participantRepository.findById(usedParticipantId);

        // then - QR 코드가 사용 완료가 되어야 한다.
        assertThat(usedParticipantId).isEqualTo(participant.getParticipantId());
        assertThat(usedParticipant.get().getIsParticipated()).isTrue();
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