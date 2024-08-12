package fete.be.domain.banner.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BannerDto {
    private Long bannerId;
    private String title;
    private String imageUrl;
    private Long posterId;
}
