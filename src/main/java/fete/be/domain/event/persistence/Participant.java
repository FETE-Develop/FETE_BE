package fete.be.domain.event.persistence;

import fete.be.domain.event.persistence.Event;
import fete.be.domain.payment.Payment;
import jakarta.persistence.*;
import lombok.Getter;
import fete.be.domain.member.persistence.Member;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_id")
    private Payment payment;  // 결제 상태

    private String createdAt;  // 생성일자
    private String updatedAt;  // 수정일자


    // 생성 메서드
    public static Participant createParticipant(Member member, Event event) {
        Participant participant = new Participant();

        participant.member = member;
        participant.event = event;

        participant.payment = Payment.createPayment(member, event);

        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        participant.createdAt = currentTime;
        participant.updatedAt = currentTime;

        return participant;
    }
}
