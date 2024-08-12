package fete.be.domain.banner.web;

import fete.be.domain.banner.application.BannerService;
import fete.be.domain.banner.application.dto.response.BannerDto;
import fete.be.domain.banner.application.dto.response.GetBannersResponse;
import fete.be.global.util.ApiResponse;
import fete.be.global.util.Logging;
import fete.be.global.util.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/banners")
@Slf4j
public class BannerController {

    private final BannerService bannerService;


    /**
     * 배너 전체 조회 API
     *
     * @return ApiResponse<GetBannersResponse>
     */
    @GetMapping
    public ApiResponse<GetBannersResponse> getBanners() {
        try {
            log.info("GetBanners API");
            Logging.time();

            // 배너 전체 조회 (페이징 없이)
            List<BannerDto> banners = bannerService.getBanners();
            GetBannersResponse result = new GetBannersResponse(banners);

            return new ApiResponse<>(ResponseMessage.BANNER_GET_BANNERS.getCode(), ResponseMessage.BANNER_GET_BANNERS.getMessage(), result);
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.BANNER_GET_BANNERS_FAIL.getCode(), e.getMessage());
        }
    }
}
