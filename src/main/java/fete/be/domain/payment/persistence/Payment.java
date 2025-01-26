package fete.be.domain.payment.persistence;

import fete.be.domain.event.persistence.Event;
import fete.be.domain.member.persistence.Member;
import fete.be.domain.payment.application.dto.response.TossPaymentResponse;
import fete.be.domain.ticket.persistence.Participant;
import fete.be.global.util.UUIDGenerator;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;  // 결제 유저

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;  // 결제할 이벤트

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "participant_id")
    private Participant participant;  // 유저의 이벤트 참여 정보

    @Column(name = "ticket_type")
    private String ticketType;  // 티켓 종류 - 얼리버드 / 현장구매 / 프로모션
    @Column(name = "ticket_price")
    private int ticketPrice;  // 티켓 가격

    @Column(name = "is_paid")
    private Boolean isPaid;  // 결제 상태 : 지불 = true, 미지불 = false
    @Column(name = "payment_code")
    private String paymentCode;  // 결제 시, 발급 받는 고유의 결제번호

    // 토스에서 제공하는 응답 값 (중요)-----------
    private int totalAmount;  // 총 결제 금액
    private String method;  // 결제 수단
    private String paymentKey;  // 결제 키 값
    private String orderId;  // 주문번호

    // 결제 취소
    private String lastTransactionKey;  // 취소 거래 키 값
    private String cancelReason;  // 취소 사유
    //---------------------------------------

    @Column(name = "created_at")
    private LocalDateTime createdAt;  // 생성일자
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;  // 수정일자
    @Column(name = "payment_at")
    private LocalDateTime paymentAt;  // 결제일자
    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;  // 취소일자
    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_status")
    private TicketStatus ticketStatus;  // 티켓 상태 : 예매 완료, 취소


    // 생성 메서드
    public static Payment createPayment(Member member, Event event, Participant participant, String ticketType, int ticketPrice) {
        Payment payment = new Payment();

        payment.member = member;
        payment.event = event;
        payment.participant = participant;

        payment.ticketType = ticketType;
        payment.ticketPrice = ticketPrice;
        payment.isPaid = false;  // 처음 생성 시, 결제 미완료 상태로 저장
        payment.totalAmount = 0;  // 아직 결제 전이기 때문에, 결제 금액을 0원으로 저장

        LocalDateTime currentTime = LocalDateTime.now();
        payment.createdAt = currentTime;
        payment.updatedAt = currentTime;

        return payment;
    }

    // 결제 완료로 전환
    public static void completePayment(Payment payment) {
        payment.isPaid = true;
        payment.ticketStatus = TicketStatus.UNUSED;
        payment.paymentAt = LocalDateTime.now();
        payment.updatedAt = LocalDateTime.now();
    }

    // 결제 취소로 전환
    public static void cancelPayment(Payment payment) {
        payment.isPaid = false;
        payment.ticketStatus = TicketStatus.CANCEL;
        payment.totalAmount = 0;  // 결제 취소되었기 때문에 결제 금액을 0원으로 변경
        payment.canceledAt = LocalDateTime.now();
        payment.updatedAt = LocalDateTime.now();
    }

    // 결제번호 생성
    public static void generatePaymentCode(Payment payment, String paymentCode) {
        payment.paymentCode = paymentCode;
        payment.updatedAt = LocalDateTime.now();
    }

    // 토스 응답 값 업데이트 메서드
    public static Payment updateTossPaymentInfo(Payment payment, TossPaymentResponse tossPaymentResponse) {
        payment.totalAmount = payment.ticketPrice;  // 추후에 할인 쿠폰이 생긴다면, 할인 쿠폰 정보 가져와서 차감해주면 됨

        // 카드사 정보가 존재한다면 함께 전달
        if (tossPaymentResponse.getCard() == null || tossPaymentResponse.getCard().getIssuerCode() == null) {
            payment.method = tossPaymentResponse.getMethod();
        } else {
            String issuerCode = tossPaymentResponse.getCard().getIssuerCode();
            String cardName = "(" + CardCode.convertCardCode(issuerCode) + ")";
            payment.method = tossPaymentResponse.getMethod() + cardName;
        }

        payment.paymentKey = tossPaymentResponse.getPaymentKey();
        payment.orderId = tossPaymentResponse.getOrderId();
        payment.paymentAt = LocalDateTime.now();
        payment.updatedAt = LocalDateTime.now();

        return payment;
    }

    // 토스 결제 취소 응답 값 업데이트
    public static Payment updateTossCancelInfo(Payment payment, String lastTransactionKey, String cancelReason) {
        payment.lastTransactionKey = lastTransactionKey;
        payment.cancelReason = cancelReason;
        payment.canceledAt = LocalDateTime.now();
        payment.updatedAt = LocalDateTime.now();

        return payment;
    }

    // 무료 티켓 취소 시, 필드 업데이트
    public static Payment updateCancelInfo(Payment payment, String cancelReason) {
        payment.cancelReason = cancelReason;
        payment.canceledAt = LocalDateTime.now();
        payment.updatedAt = LocalDateTime.now();

        return payment;
    }

    // 사용 완료된 티켓으로 업데이트
    public static void completeTicket(Payment payment) {
        payment.ticketStatus = TicketStatus.COMPLETE;
        payment.updatedAt = LocalDateTime.now();
    }
}
