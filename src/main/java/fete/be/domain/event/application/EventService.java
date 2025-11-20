package fete.be.domain.event.application;

import fete.be.domain.event.application.dto.request.BuyTicketDto;
import fete.be.domain.event.application.dto.request.BuyTicketRequest;
import fete.be.domain.event.application.dto.request.CheckTicketsQuantityRequest;
import fete.be.domain.event.exception.*;
import fete.be.domain.event.persistence.Event;
import fete.be.domain.event.persistence.Ticket;
import fete.be.domain.event.persistence.TicketRepository;
import fete.be.domain.payment.application.TossService;
import fete.be.domain.payment.application.dto.request.TossPaymentRequest;
import fete.be.domain.payment.persistence.PaymentRepository;
import fete.be.domain.poster.persistence.PosterManager;
import fete.be.domain.ticket.persistence.Participant;
import fete.be.domain.ticket.persistence.ParticipantRepository;
import fete.be.domain.member.application.MemberService;
import fete.be.domain.member.persistence.Member;
import fete.be.domain.payment.persistence.Payment;
import fete.be.domain.poster.application.PosterService;
import fete.be.domain.poster.persistence.Poster;
import fete.be.global.util.ResponseMessage;
import fete.be.global.util.UUIDGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


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
    private final PaymentRepository paymentRepository;
    private final TicketRepository ticketRepository;


    @Transactional
    public List<String> buyTicket(Long posterId, BuyTicketRequest buyTicketRequest) {
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

        // 변경사항이 발생할 티켓 id 리스트
        List<Long> ticketIds = poster.getEvent().getTickets()
                .stream()
                .map(Ticket::getTicketId)
                .collect(Collectors.toList());

        // 결제 정보 검증
        List<BuyTicketDto> requestTickets = buyTicketRequest.getTickets();
        for (BuyTicketDto requestTicket : requestTickets) {
            isValidRequest(requestTicket, member, poster, participants);
        }

        // 총 결제 요청 금액 계산
        int requestAmount = getRequestAmount(requestTickets);

        // 여기에 payment.isPaid가 false일 때만 결제 실행되도록 로직 추가하기 -> 결제 중복 방지
        Boolean isInitialPayment = true;
        for (Participant participant : participants) {
            Payment payment = participant.getPayment();
            Boolean isPaid = payment.getIsPaid();
            if (isPaid) {
                isInitialPayment = false;
                break;
            }
        }

        // 이미 결제된 상태일 경우
        if (!isInitialPayment) {
            throw new AlreadyPaymentStateException(ResponseMessage.EVENT_ALREADY_PAYMENT_STATE.getMessage());
        }

        // 판매된 티켓 개수 업데이트
        updateSoldTicketCount(ticketIds, requestTickets);

        try {
            // 결제 시스템 실행
            qrCodes = paymentSystem(requestAmount, participants, buyTicketRequest.getTossPaymentRequest(), qrCodes);
        } catch (Exception e) {
            // 토스 페이먼츠 결제 승인 API에서 장애 발생 시
            throw new TossPaymentException(ResponseMessage.TOSS_PAYMENT_FAILURE.getMessage());
        }

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
            String paymentCode = UUIDGenerator.generateUUID();
            for (Participant participant : participants) {
                updatePaymentInfo(participant, paymentCode);
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
        String paymentCode = UUIDGenerator.generateUUID();
        for (Participant participant : paidParticipants) {
            updatePaymentInfo(participant, paymentCode);
            qrCodes.add(makeQRCode(participant));
        }

        return qrCodes;
    }

    /**
     * 결제 정보 업데이트
     */
    private void updatePaymentInfo(Participant participant, String paymentCode) {
        // 결제 완료로 변경
        Payment.completePayment(participant.getPayment());
        // 총 수익 반영
        Event.updateTotalProfit(participant.getEvent(), participant.getPayment().getTotalAmount());
        // 결제번호 발급
        Payment.generatePaymentCode(participant.getPayment(), paymentCode);
    }

    /**
     * 결제 요청 정보 검증
     */
    public void isValidRequest(BuyTicketDto requestTicket, Member member, Poster poster, List<Participant> participants) {

        // 실제 DB의 티켓 정보
        List<Ticket> tickets = poster.getEvent().getTickets();

        // 요청 결제 정보
        int ticketPrice = requestTicket.getTicketPrice();
        int ticketNumber = requestTicket.getTicketNumber();
        String ticketType = requestTicket.getTicketType();

        // 티켓 정보 검증
        canBuy(requestTicket, tickets);

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
     * 판매된 티켓 개수 업데이트 (비관적 락 적용)
     */
    public void updateSoldTicketCount(List<Long> ticketIds, List<BuyTicketDto> requestTickets) {
        for (Long ticketId : ticketIds) {
            // 비관적 락으로 티켓 조회
            Ticket ticket = ticketRepository.findByIdForUpdate(ticketId)
                    .orElseThrow(() -> new NotFoundTicketException(ResponseMessage.TICKET_NO_EXIST.getMessage()));

            for (BuyTicketDto requestTicket : requestTickets) {
                int ticketNumber = requestTicket.getTicketNumber();
                String ticketType = requestTicket.getTicketType();

                // 판매된 티켓 개수만큼 업데이트
                if (ticket.getTicketType().equals(ticketType)) {
                    Ticket.updateSoldTicketCount(ticket, ticketNumber);
                }
            }
        }
    }

    /**
     * 판매된 티켓 개수 업데이트 (old 버전)
     */
//    public void updateSoldTicketCount(List<Ticket> tickets, List<BuyTicketDto> requestTickets) {
//        for (BuyTicketDto requestTicket : requestTickets) {
//            int ticketNumber = requestTicket.getTicketNumber();
//            String ticketType = requestTicket.getTicketType();
//
//            // 판매된 티켓 개수만큼 업데이트
//            for (Ticket ticket : tickets) {
//                if (ticket.getTicketType().equals(ticketType)) {
//                    Ticket.updateSoldTicketCount(ticket, ticketNumber);
//                }
//            }
//        }
//    }

    /**
     * 이벤트 참여 객체 생성해주는 메서드
     */
    private Participant makeParticipants(Member member, Poster poster, String ticketType, int ticketPrice) {
        Participant participant = Participant.createParticipant(member, poster.getEvent());
        Participant savedParticipant = participantRepository.save(participant);

        Payment payment = Payment.createPayment(member, poster.getEvent(), savedParticipant, ticketType, ticketPrice);
        Payment savedPayment = paymentRepository.save(payment);

        Participant result = Participant.setPayment(savedParticipant, savedPayment);
        result.getEvent().setParticipants(result, poster.getEvent());

        return result;
    }

    /**
     * Participant에 QR 코드를 1개 발급해주는 메서드
     */
    private String makeQRCode(Participant participant) throws Exception {
        // QR 코드 발급
        String qrCodeBase64 = qrCodeService.generateQRCodeBase64(participant, 250, 250);
        return qrCodeBase64;
    }

    /**
     * 구매하려는 티켓의 수량이 충분한지 확인하는 메서드
     */
    public void checkTicketsQuantity(Long posterId, CheckTicketsQuantityRequest request) {
        // posterId로 포스터 찾기
        Poster poster = posterService.findPosterByPosterId(posterId);

        // 실제 DB의 티켓 정보
        List<Ticket> tickets = poster.getEvent().getTickets();

        // 결제 정보 검증
        List<BuyTicketDto> requestTickets = request.getTickets();
        for (BuyTicketDto requestTicket : requestTickets) {
            canBuy(requestTicket, tickets);
        }
    }

    /**
     * 임시 담당자 권한 부여하는 메서드
     */
    @Transactional
    public void grantTempManager(String managerCode) {
        // 포스터에서 managerCode로 포스터 조회
        Poster poster = posterService.findPosterByManagerCode(managerCode);

        // 현재 유저 조회
        Member member = memberService.findMemberByEmail();

        // 조회한 포스터 담당자에 현재 유저 추가 & 멤버와 해당 포스터 연결
        PosterManager posterManager = PosterManager.createPosterManager(member, poster);
        poster.addPosterManager(posterManager);
        member.addPosterManager(posterManager);
    }

    private void canBuy(BuyTicketDto requestTicket, List<Ticket> tickets) {
        // 요청 결제 정보
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
                    throw new InsufficientTicketsException(ResponseMessage.TICKET_NOT_ENOUGH_QUANTITY.getMessage());
                }
                // 결제 요청된 티켓 가격과 실제 티켓 가격 비교
                if (ticket.getTicketPrice() != ticketPrice) {
                    throw new IncorrectTicketPriceException(ResponseMessage.TICKET_INVALID_AMOUNT.getMessage());
                }
            }
        }

        // 티켓의 종류가 일치하는 것이 없는 경우
        if (!findTicketType) {
            throw new IncorrectTicketTypeException(ResponseMessage.TICKET_INVALID_TYPE.getMessage());
        }
    }

    public void checkEventManager(Long posterId) {
        Member member = memberService.findMemberByEmail();
        Poster poster = posterService.findPosterByPosterId(posterId);

        Member mainManager = poster.getMember();
        List<PosterManager> posterManagers = poster.getPosterManagers();

        // 임시 담당자인지 검사
        boolean isTempManager = false;
        for(PosterManager posterManager : posterManagers) {
            if (posterManager.getMember().equals(member)) {
                isTempManager = true;
                break;
            }
        }

        if (!isTempManager && !mainManager.equals(member)) {  // 임시 담당자 목록 또는 등록 담당자가 아닐 경우
            throw new IncorrectEventManagerException(ResponseMessage.EVENT_INCORRECT_MANAGER.getMessage());
        }
    }
}
