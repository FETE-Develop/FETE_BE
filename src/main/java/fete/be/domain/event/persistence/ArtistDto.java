package fete.be.domain.event.persistence;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class ArtistDto {
    private String name;
    private String infoUrl;
    private String imageUrl;
}
