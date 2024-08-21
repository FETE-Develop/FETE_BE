package fete.be.domain.ticket.persistence;

import fete.be.domain.event.persistence.Event;
import fete.be.domain.payment.persistence.Payment;
import jakarta.persistence.*;
import lombok.Getter;
import fete.be.domain.member.persistence.Member;

import java.time.LocalDateTime;

@Entity
@Getter
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private Long participantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;  // 참여한 회원

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;  // 유저가 참여한 이벤트

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "participant")
    private Payment payment;  // 결제 상태

    @Column(name = "is_participated")
    private Boolean isParticipated;  // 참여 여부
    @Column(name = "created_at")
    private LocalDateTime createdAt;  // 생성일자
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;  // 수정일자


    // 생성 메서드
    public static Participant createParticipant(Member member, Event event) {
        Participant participant = new Participant();

        participant.member = member;
        participant.event = event;

        participant.isParticipated = false;  // 초기에는 QR 코드를 생성한 상태이기 때문에 false 상태 -> QR 코드 인증 시 true로 변경
        LocalDateTime currentTime = LocalDateTime.now();
        participant.createdAt = currentTime;
        participant.updatedAt = currentTime;

        return participant;
    }

    // Payment 설정
    public static Participant setPayment(Participant participant, Payment payment) {
        participant.payment = payment;

        return participant;
    }

    // 참여 완료 메서드
    public static void completeParticipant(Participant participant) {
        participant.isParticipated = true;
    }

    // 취소 금액을 수익에 반영하는 메서드
    public void updateProfit(int amount) {
        this.event.updateProfit(amount);
    }
}
