package fete.be.domain.admin.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CreateBannerRequest {
    private String title;
    private String content;
    private String imageUrl;
    private Long posterId;
}
