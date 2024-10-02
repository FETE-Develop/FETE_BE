package fete.be.domain.banner.application.dto.response;

import fete.be.domain.banner.persistence.Banner;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BannerDto {
    private Long bannerId;
    private String title;
    private String content;
    private String imageUrl;
    private Long posterId;

    public BannerDto(Banner banner) {
        this.bannerId = banner.getBannerId();
        this.title = banner.getTitle();
        this.content = banner.getContent();
        this.imageUrl = banner.getImageUrl();
        this.posterId = banner.getPoster().getPosterId();
    }
}
