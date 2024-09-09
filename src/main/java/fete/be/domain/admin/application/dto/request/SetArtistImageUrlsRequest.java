package fete.be.domain.admin.application.dto.request;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class SetArtistImageUrlsRequest {
    List<String> imageUrls;
}
