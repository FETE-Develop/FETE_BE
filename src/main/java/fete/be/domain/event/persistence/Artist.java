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
    private String name;  // 아티스트 이름
    @Column(name = "info_url")
    private String infoUrl;  // 아티스트 정보 링크
    @Column(name = "image_url")
    private String imageUrl;  // 아티스트 프로필 이미지 (관리자만 등록 가능)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;


    // 생성 메서드
    public static Artist createArtist(ArtistDto artistDto, Event event) {
        Artist artist = new Artist();

        artist.name = artistDto.getName();
        artist.infoUrl = artistDto.getInfoUrl();
        artist.event = event;

        return artist;
    }
}
