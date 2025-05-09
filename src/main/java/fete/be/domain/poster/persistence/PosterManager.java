package fete.be.domain.poster.persistence;

import fete.be.domain.member.persistence.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Entity
@Getter
@Slf4j
public class PosterManager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "poster_manager_id")
    private Long posterManagerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poster_id")
    private Poster poster;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;  // 수정일자


    public static PosterManager createPosterManager(Member member, Poster poster) {
        PosterManager posterManager = new PosterManager();

        posterManager.member = member;
        posterManager.poster = poster;
        posterManager.updatedAt = LocalDateTime.now();

        return posterManager;
    }
}
