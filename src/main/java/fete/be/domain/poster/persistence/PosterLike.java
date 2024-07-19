package fete.be.domain.poster.persistence;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class PosterLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "poster_like_id")
    private Long posterLikeId;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "poster_id")
    private Long posterId;


    // 생성 메서드
    public static PosterLike createPosterLike(Long memberId, Long posterId) {
        PosterLike posterLike = new PosterLike();
        posterLike.memberId = memberId;
        posterLike.posterId = posterId;

        return posterLike;
    }
}
