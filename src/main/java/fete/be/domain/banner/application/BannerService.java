package fete.be.domain.banner.application;

import fete.be.domain.admin.application.dto.request.CreateBannerRequest;
import fete.be.domain.banner.application.dto.response.BannerDto;
import fete.be.domain.banner.persistence.Banner;
import fete.be.domain.banner.persistence.BannerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class BannerService {

    private final BannerRepository bannerRepository;


    @Transactional
    public Long createBanner(CreateBannerRequest request) {
        Banner banner = Banner.createBanner(request);
        Banner savedBanner = bannerRepository.save(banner);

        return savedBanner.getBannerId();
    }

    public List<BannerDto> getBanners() {
        List<BannerDto> banners = bannerRepository.findAll().stream()
                .map(banner -> new BannerDto(
                        banner.getBannerId(),
                        banner.getTitle(),
                        banner.getImageUrl(),
                        banner.getPosterId()
                ))
                .collect(Collectors.toList());

        return banners;
    }
}
