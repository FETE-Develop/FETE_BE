package fete.be.domain.payment;

import fete.be.domain.event.persistence.Event;
import fete.be.domain.event.persistence.Participant;
import fete.be.domain.member.persistence.Member;
import jakarta.persistence.*;
import lombok.Getter;

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

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "paymentState")
    private Participant participant;  // 유저의 이벤트 참여 정보

    private boolean isPaid;  // 결제 상태 : 지불 = true, 미지불 = false
    private String createdAt;  // 생성일자
    private String updatedAt;  // 수정일자
}
