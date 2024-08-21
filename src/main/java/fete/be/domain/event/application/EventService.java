package fete.be.domain.event.application;

import fete.be.domain.event.application.dto.BuyTicketDto;
import fete.be.domain.event.application.dto.BuyTicketRequest;
import fete.be.domain.event.exception.IncorrectPaymentAmountException;
import fete.be.domain.event.exception.IncorrectTicketPriceException;
import fete.be.domain.event.exception.IncorrectTicketTypeException;
import fete.be.domain.event.exception.InsufficientTicketsException;
import fete.be.domain.event.persistence.Event;
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
        // Participant 객체 생성
        List<Participant> participants = new ArrayList<>();

        // 현재 API 요청을 보낸 Member 찾기
        Member member = memberService.findMemberByEmail();

        // posterId로 포스터 찾기
        Poster poster = posterService.findPosterByPosterId(posterId);

        // 실제 DB의 티켓 정보
        List<Ticket> tickets = poster.getEvent().getTickets();

        // 결제 정보 검증
        List<BuyTicketDto> requestTickets = buyTicketRequest.getTickets();
        for (BuyTicketDto requestTicket : requestTickets) {
            isValidRequest(requestTicket, member, poster, participants);
        }

        // 총 결제 요청 금액 계산
        int requestAmount = getRequestAmount(requestTickets);

        // 결제 시스템 실행
        qrCodes = paymentSystem(requestAmount, participants, buyTicketRequest.getTossPaymentRequest(), qrCodes);

        // 판매된 티켓 개수 업데이트
        updateSoldTicketCount(tickets, requestTickets);

        return qrCodes;
    }

    /**
     * 결제 시스템
     */
    public List<String> paymentSystem(int requestAmount, List<Participant> participants,
                                      TossPaymentRequest tossPaymentRequest, List<String> qrCodes) throws Exception {

        // 1. 무료 이벤트일 때
        if (requestAmount == FREE) {
            // 티켓 개수만큼 QR 코드 발급
            for (Participant participant : participants) {
                qrCodes.add(makeQRCode(participant));
            }
            return qrCodes;
        }

        // 2. 유료 이벤트일 때
        // 토스 페이먼츠 결제 시스템

        // 1) 프론트로부터 TossPaymentRequest를 받아오기 - 이벤트 신청할 때 프론트에서 토스 API로부터 인가코드를 받아와서 백엔드로 전달해줘야 함
        log.info("TossPaymentRequest={}", tossPaymentRequest);

        // 토스로 결제 승인 보내기 전, 가격 검증 로직 실행
        int tossAmount = tossPaymentRequest.getAmount();
        if (requestAmount != tossAmount) {
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
     * 결제 요청 정보 검증
     */
    public void isValidRequest(BuyTicketDto requestTicket, Member member, Poster poster, List<Participant> participants) {

        // 실제 DB의 티켓 정보
        List<Ticket> tickets = poster.getEvent().getTickets();

        int ticketPrice = requestTicket.getTicketPrice();
        int ticketNumber = requestTicket.getTicketNumber();
        String ticketType = requestTicket.getTicketType();

        // 결제 요청 정보와 실제 DB 정보와 일치하는지 검증
        boolean findTicketType = false;
        for (Ticket ticket : tickets) {
            if (ticket.getTicketType().equals(ticketType)) {
                findTicketType = true;
                // 결제 요청된 티켓의 수량만큼 구매 가능한지 검사
                if (!Ticket.canBuyTicket(ticket, ticketNumber)) {
                    throw new InsufficientTicketsException("티켓의 수량이 충분하지 않습니다.");
                }
                // 결제 요청된 티켓 가격과 실제 티켓 가격 비교
                if (ticket.getTicketPrice() != ticketPrice) {
                    throw new IncorrectTicketPriceException("티켓의 가격이 올바르지 않습니다.");
                }
            }
        }

        // 티켓의 종류가 일치하는 것이 없는 경우
        if (!findTicketType) {
            throw new IncorrectTicketTypeException("티켓의 종류가 올바르지 않습니다.");
        }

        // 티켓 개수만큼 Participant 객체 생성해서 리스트에 넣기
        for (int i = 0; i < ticketNumber; i++) {
            participants.add(makeParticipants(member, poster, ticketType, ticketPrice));
        }
    }

    /**
     * 총 결제 요청 금액 계산
     */
    public int getRequestAmount(List<BuyTicketDto> requestTickets) {
        int amount = 0;
        for (BuyTicketDto requestTicket : requestTickets) {
            amount += requestTicket.getTicketPrice() * requestTicket.getTicketNumber();
        }

        return amount;
    }

    /**
     * 판매된 티켓 개수 업데이트
     */
    public void updateSoldTicketCount(List<Ticket> tickets, List<BuyTicketDto> requestTickets) {
        for (BuyTicketDto requestTicket : requestTickets) {
            int ticketNumber = requestTicket.getTicketNumber();
            String ticketType = requestTicket.getTicketType();

            // 판매된 티켓 개수만큼 업데이트
            for (Ticket ticket : tickets) {
                if (ticket.getTicketType().equals(ticketType)) {
                    Ticket.updateSoldTicketCount(ticket, ticketNumber);
                }
            }
        }
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

        // 총 수익 반영
        Event.updateTotalProfit(participant.getEvent(), participant.getPayment().getTotalAmount());

        // QR 코드 발급
        String qrCodeBase64 = qrCodeService.generateQRCodeBase64(participant, 250, 250);
        return qrCodeBase64;
    }
}
