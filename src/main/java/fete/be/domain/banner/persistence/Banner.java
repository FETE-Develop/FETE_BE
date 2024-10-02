package fete.be.domain.banner.persistence;

import fete.be.domain.admin.application.dto.request.CreateBannerRequest;
import fete.be.domain.admin.application.dto.request.ModifyBannerRequest;
import fete.be.domain.poster.application.PosterService;
import fete.be.domain.poster.persistence.Poster;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "banner_id")
    private Long bannerId;

    @Column(name = "title")
    private String title;  // 배너 제목
    @Column(name = "content")
    private String content;  // 배너 내용
    @Column(name = "image_url")
    private String imageUrl;  // 배너 이미지
    @OneToOne(mappedBy = "banner", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Poster poster;  // 연결된 포스터


    // 생성 메서드
    public static Banner createBanner(CreateBannerRequest request, PosterService posterService) {
        Banner banner = new Banner();

        banner.title = request.getTitle();
        banner.content = request.getContent();
        banner.imageUrl = request.getImageUrl();

        // 포스터 연결
        Long posterId = request.getPosterId();
        Poster poster = posterService.findPosterByPosterId(posterId);
        banner.poster = poster;
        poster.setBanner(banner);

        return banner;
    }

    // 업데이트 메서드
    public static Banner modifyBanner(Banner banner, ModifyBannerRequest request, PosterService posterService) {
        banner.title = request.getTitle();
        banner.content = request.getContent();
        banner.imageUrl = request.getImageUrl();

        // 포스터 연결
        Long posterId = request.getPosterId();
        Poster poster = posterService.findPosterByPosterId(posterId);
        banner.poster = poster;
        poster.setBanner(banner);

        return banner;
    }

    // 삭제 메서드
    public static void deleteBanner(Banner banner) {
        Poster poster = banner.poster;
        poster.setBanner(null);
    }
}
