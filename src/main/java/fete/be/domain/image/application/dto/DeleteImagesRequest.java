package fete.be.domain.image.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
public class DeleteImagesRequest {
    private List<String> fileUrls;
}
