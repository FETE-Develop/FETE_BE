package fete.be.domain.image.application.dto;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class DeleteImagesRequest {
    private List<String> fileUrls;
}
