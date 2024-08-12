package fete.be.domain.banner.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
public class GetBannersResponse {
    private List<BannerDto> banners;
}
