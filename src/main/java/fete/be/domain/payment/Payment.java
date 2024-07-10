package fete.be.domain.payment;

import fete.be.domain.event.persistence.Event;
import fete.be.domain.event.persistence.Participant;
import fete.be.domain.member.persistence.Member;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "payment")
    private Participant participant;  // 유저의 이벤트 참여 정보

    @Column(name = "is_paid")
    private Boolean isPaid;  // 결제 상태 : 지불 = true, 미지불 = false
    @Column(name = "created_at")
    private String createdAt;  // 생성일자
    @Column(name = "updated_at")
    private String updatedAt;  // 수정일자


    // 생성 메서드
    public static Payment createPayment(Member member, Event event) {
        Payment payment = new Payment();

        payment.member = member;
        payment.event = event;
        payment.isPaid = false;  // 처음 생성 시, 결제 미완료 상태로 저장

        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        payment.createdAt = currentTime;
        payment.updatedAt = currentTime;

        return payment;
    }

    // 결제 완료로 전환
    public static void completePayment(Payment payment) {
        payment.isPaid = true;
    }
}
