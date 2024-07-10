package fete.be.domain.event.application;

import fete.be.domain.event.persistence.Participant;
import fete.be.domain.event.persistence.ParticipantRepository;
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
    private final ParticipantRepository participantRepository;


    public String applyEvent(Long posterId) throws Exception {
        // 현재 API 요청을 보낸 Member 찾기
        Member member = memberService.findMemberByEmail();

        // posterId로 포스터 찾기
        Poster poster = posterService.findPosterByPosterId(posterId);

        // 이벤트 참여 객체 생성
        Participant participant = Participant.createParticipant(member, poster.getEvent());
        Participant savedParticipant = participantRepository.save(participant);

        // 결제 시스템 실행 - 하지만 지금은 무료라고 가정하고 진행
        // 1. 무료 이벤트일 때
        if (savedParticipant.getEvent().getTicketPrice() == FREE) {
            // 결제 완료로 변경
            Payment.completePayment(savedParticipant.getPayment());

            // QR 코드 발급
            String qrCodeBase64 = qrCodeService.generateQRCodeBase64(savedParticipant, 250, 250);
            return qrCodeBase64;
        }

        // 2. 유료 이벤트일 때
        // 여기서 결제 시스템 실행

        // 결제가 성공적으로 완료될 때, QR 코드 발급
        String qrCodeBase64 = "";

        return qrCodeBase64;
    }
}
