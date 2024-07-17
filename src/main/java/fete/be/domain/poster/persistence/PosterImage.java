package fete.be.domain.poster.persistence;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class PosterImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "poster_image_id")
    private Long posterImageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poster_id")
    private Poster poster;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;


    // 생성 메서드
    public static PosterImage createPosterImage(Poster poster, String imageUrl) {
        PosterImage posterImage = new PosterImage();
        posterImage.poster = poster;
        posterImage.imageUrl = imageUrl;

        return posterImage;
    }
}
