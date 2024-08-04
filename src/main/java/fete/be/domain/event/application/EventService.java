package fete.be.domain.event.application;

import fete.be.domain.event.application.dto.BuyTicketRequest;
import fete.be.domain.event.exception.IncorrectPaymentAmountException;
import fete.be.domain.event.persistence.Ticket;
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

import java.util.ArrayList;
import java.util.List;


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
    public List<String> buyTicket(Long posterId, BuyTicketRequest buyTicketRequest) throws Exception {
        // QR 코드 리스트 선언
        List<String> qrCodes = new ArrayList<>();
        List<Participant> participants = new ArrayList<>();

        // 현재 API 요청을 보낸 Member 찾기
        Member member = memberService.findMemberByEmail();

        // posterId로 포스터 찾기
        Poster poster = posterService.findPosterByPosterId(posterId);

        // ---------------결제 가격 검증 로직 시작---------------
        int ticketPrice = buyTicketRequest.getTicketPrice();
        int ticketNumber = buyTicketRequest.getTicketNumber();
        String ticketType = buyTicketRequest.getTicketType();

        // 실제 DB에 저장되어 있는 가격 불러오기
        int originTicketPrice = 0;
        List<Ticket> tickets = poster.getEvent().getTickets();
        for (Ticket ticket : tickets) {
            if (ticket.getTicketType().equals(ticketType)) {
                originTicketPrice = ticket.getTicketPrice();
            }
        }

        // 결제 요청된 가격과 실제 이벤트 티켓의 가격 비교
        int originAmount = originTicketPrice * ticketNumber;
        int requestAmount = ticketPrice * ticketNumber;
        if (originAmount != requestAmount) {
            throw new IncorrectPaymentAmountException("결제 가격이 올바르지 않습니다.");
        }
        // ---------------결제 가격 검증 로직 끝---------------

        // 티켓 개수만큼 Participant 객체 생성해서 리스트에 넣기
        for (int i = 0; i < ticketNumber; i++) {
            participants.add(makeParticipants(member, poster, ticketType, ticketPrice));
        }

        // 결제 시스템 실행
        // 1. 무료 이벤트일 때
        if (requestAmount == FREE) {
            for (Participant participant : participants) {
                qrCodes.add(makeQRCode(participant));
            }
            return qrCodes;
        }

        // 2. 유료 이벤트일 때
        // 토스 페이먼츠 결제 시스템

        // 1) 프론트로부터 TossPaymentRequest를 받아오기 - 이벤트 신청할 때 프론트에서 토스 API로부터 인가코드를 받아와서 백엔드로 전달해줘야 함
        TossPaymentRequest tossPaymentRequest = buyTicketRequest.getTossPaymentRequest();
        log.info("TossPaymentRequest={}", tossPaymentRequest);

        // 토스로 결제 승인 보내기 전, 가격 검증 로직 실행
        int tossAmount = tossPaymentRequest.getAmount();
        if (originAmount != tossAmount) {
            throw new IncorrectPaymentAmountException("결제 가격이 올바르지 않습니다.");
        }

        // 2) TossPaymentRequest로 토스의 결제 승인 API 요청
        List<Participant> paidParticipants = tossService.executePayment(tossPaymentRequest, participants);

        // 3) 결제가 성공적으로 완료되었을 때, QR 코드 발급
        for (Participant participant : paidParticipants) {
            qrCodes.add(makeQRCode(participant));
        }

        return qrCodes;
    }

    /**
     * 이벤트 참여 객체 생성해주는 메서드
     */
    private Participant makeParticipants(Member member, Poster poster, String ticketType, int ticketPrice) {
        Participant participant = Participant.createParticipant(member, poster.getEvent(), ticketType, ticketPrice);
        Participant savedParticipant = participantRepository.save(participant);
        return savedParticipant;
    }

    /**
     * Participant에 QR 코드를 1개 발급해주는 메서드
     */
    private String makeQRCode(Participant participant) throws Exception {
        // 결제 완료로 변경
        Payment.completePayment(participant.getPayment());

        // QR 코드 발급
        String qrCodeBase64 = qrCodeService.generateQRCodeBase64(participant, 250, 250);
        return qrCodeBase64;
    }
}
