package fete.be.domain.event.persistence;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class ArtistDto {
    private String name;
    private String imageUrl;
}
