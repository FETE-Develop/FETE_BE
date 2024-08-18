package fete.be.domain.banner.persistence;

import fete.be.domain.admin.application.dto.request.CreateBannerRequest;
import fete.be.domain.admin.application.dto.request.ModifyBannerRequest;
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
    @Column(name = "poster_id")
    private Long posterId;  // 배너와 연결된 포스터


    // 생성 메서드
    public static Banner createBanner(CreateBannerRequest request) {
        Banner banner = new Banner();

        banner.title = request.getTitle();
        banner.content = request.getContent();
        banner.imageUrl = request.getImageUrl();
        banner.posterId = request.getPosterId();

        return banner;
    }

    // 업데이트 메서드
    public static Banner modifyBanner(Banner banner, ModifyBannerRequest request) {
        banner.title = request.getTitle();
        banner.content = request.getContent();
        banner.imageUrl = request.getImageUrl();
        banner.posterId = request.getPosterId();

        return banner;
    }
}
