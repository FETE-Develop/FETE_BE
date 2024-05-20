package fete.be.domain.participant;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import fete.be.domain.member.Member;
import fete.be.domain.poster.Poster;

@Entity
@Getter
@Setter
public class Participant {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private Long participantId;

    private boolean paid;  // 결제 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poster_id")
    private Poster poster;  // 참여한 포스터 (이벤트)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;  // 참여한 회원
}
