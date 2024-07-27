package fete.be.domain.event.application;

import fete.be.domain.payment.application.TossService;
import fete.be.domain.payment.application.dto.request.TossPaymentRequest;
import fete.be.domain.ticket.persistence.Participant;
import fete.be.domain.ticket.persistence.ParticipantRepository;
import fete.be.domain.member.application.MemberService;
import fete.be.domain.member.persistence.Member;
import fete.be.domain.payment.persistence.Payment;
import fete.be.domain.poster.application.PosterService;
import fete.be.domain.poster.persistence.Poster;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventService {

    private static final int FREE = 0;

    private final MemberService memberService;
    private final PosterService posterService;
    private final QRCodeService qrCodeService;
    private final TossService tossService;
    private final ParticipantRepository participantRepository;


    @Transactional
    public String applyEvent(Long posterId, TossPaymentRequest tossPaymentRequest) throws Exception {
        log.info("TossPaymentRequest={}", tossPaymentRequest);

        // 현재 API 요청을 보낸 Member 찾기
        Member member = memberService.findMemberByEmail();

        // posterId로 포스터 찾기
        Poster poster = posterService.findPosterByPosterId(posterId);

        // 이벤트 참여 객체 생성
        Participant participant = Participant.createParticipant(member, poster.getEvent());
        Participant savedParticipant = participantRepository.save(participant);

        // 결제 시스템 실행
        // 1. 무료 이벤트일 때
        if (savedParticipant.getEvent().getTicketPrice() == FREE) {
            // 결제 완료로 변경
            Payment.completePayment(savedParticipant.getPayment());

            // QR 코드 발급
            String qrCodeBase64 = qrCodeService.generateQRCodeBase64(savedParticipant, 250, 250);
            return qrCodeBase64;
        }

        // 2. 유료 이벤트일 때
        // 토스 페이먼츠 결제 시스템

        // 1) 프론트로부터 TossPaymentRequest를 받아오기 - 이벤트 신청할 때 프론트에서 토스 API로부터 인가코드를 받아와서 백엔드로 전달해줘야 함
        // -> 전달 받은 TossPaymentRequest 매개 변수를 사용하면 됨
        log.info("Before executePayment");
        // 2) TossPaymentRequest로 토스의 결제 승인 API 요청
        Participant paidParticipant = tossService.executePayment(tossPaymentRequest, savedParticipant);

        // 3) 결제가 성공적으로 완료되었을 때, QR 코드 발급
        String qrCodeBase64 = qrCodeService.generateQRCodeBase64(paidParticipant, 250, 250);

        return qrCodeBase64;
    }
}
