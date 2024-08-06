package fete.be.domain.event.persistence;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artist_id")
    private Long artistId;

    @Column(name = "name")
    private String name;
    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;


    // 생성 메서드
    public static Artist createArtist(ArtistDto artistDto, Event event) {
        Artist artist = new Artist();

        artist.name = artistDto.getName();
        artist.imageUrl = artistDto.getImageUrl();
        artist.event = event;

        return artist;
    }
}
