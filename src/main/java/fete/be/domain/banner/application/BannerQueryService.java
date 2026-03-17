package fete.be.domain.banner.application;

import fete.be.domain.banner.application.dto.response.BannerDto;
import fete.be.domain.banner.persistence.BannerQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerQueryService {

    private final BannerQueryMapper bannerQueryMapper;

    public List<BannerDto> getBanners() {
        return bannerQueryMapper.findActiveBanners();
    }

}
