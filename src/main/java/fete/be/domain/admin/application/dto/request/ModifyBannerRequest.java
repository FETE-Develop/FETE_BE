package fete.be.domain.admin.application.dto.request;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ModifyBannerRequest {
    private String title;
    private String content;
    private String imageUrl;
    private Long posterId;
}
