package fete.be.domain.banner.application;

import fete.be.domain.admin.application.dto.request.CreateBannerRequest;
import fete.be.domain.admin.application.dto.request.ModifyBannerRequest;
import fete.be.domain.banner.application.dto.response.BannerDto;
import fete.be.domain.banner.exception.AlreadyExistsPosterException;
import fete.be.domain.banner.persistence.Banner;
import fete.be.domain.banner.persistence.BannerRepository;
import fete.be.domain.poster.application.PosterService;
import fete.be.domain.poster.persistence.Poster;
import fete.be.global.util.ResponseMessage;
import fete.be.global.util.Status;
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

    private final PosterService posterService;
    private final BannerRepository bannerRepository;


    @Transactional
    public Long createBanner(CreateBannerRequest request) {
        // 연결할 posterId
        Long posterId = request.getPosterId();
        Poster poster = posterService.findPosterByPosterId(posterId);

        // 해당 포스터로 연결된 배너가 존재하는지 검사
        if (bannerRepository.existsByPoster(poster)) {
            throw new AlreadyExistsPosterException(ResponseMessage.POSTER_ALREADY_EXIST.getMessage());
        }

        Banner banner = Banner.createBanner(request, posterService);
        Banner savedBanner = bannerRepository.save(banner);

        return savedBanner.getBannerId();
    }

    @Transactional
    public Long modifyBanner(Long bannerId, ModifyBannerRequest request) {
        // 수정할 배너 조회
        Banner banner = bannerRepository.findById(bannerId).orElseThrow(
                () -> new IllegalArgumentException(ResponseMessage.BANNER_NO_EXIST.getMessage())
        );

        // 배너 업데이트 메서드 실행
        Banner modifiedBanner = Banner.modifyBanner(banner, request, posterService);
        bannerRepository.save(modifiedBanner);

        return modifiedBanner.getBannerId();
    }

    @Transactional
    public void deleteBanner(Long bannerId) {
        // 삭제할 배너 조회
        Banner banner = bannerRepository.findById(bannerId).orElseThrow(
                () -> new IllegalArgumentException(ResponseMessage.BANNER_NO_EXIST.getMessage())
        );

        // 배너와 연관된 정보 삭제
        Banner.deleteBanner(banner);

        // 배너 삭제 실행
        bannerRepository.delete(banner);
    }

    public List<BannerDto> getBanners() {
        // 배너에 연결된 포스터의 상태가 ACTIVE, BANNER인 것들만 조회할 것
        List<Status> wantedStatuses = List.of(Status.ACTIVE, Status.BANNER);

        // 조건에 맞는 배너 전체 조회
        List<BannerDto> banners = bannerRepository.findAll().stream()
                .filter(banner -> wantedStatuses.contains(banner.getPoster().getStatus()))
                .map(banner -> new BannerDto(banner))
                .collect(Collectors.toList());

        return banners;
    }
}
