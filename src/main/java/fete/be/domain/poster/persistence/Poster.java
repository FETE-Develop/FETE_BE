package fete.be.domain.poster.persistence;

import fete.be.domain.banner.persistence.Banner;
import fete.be.domain.category.persistence.Category;
import fete.be.domain.event.persistence.Event;
import fete.be.domain.image.application.ImageUploadService;
import fete.be.domain.member.persistence.Member;
import fete.be.domain.poster.application.dto.request.ModifyPosterRequest;
import fete.be.domain.poster.application.dto.request.WritePosterRequest;
import fete.be.global.util.Status;
import fete.be.global.util.UUIDGenerator;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Entity
@Getter
@Slf4j
public class Poster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "poster_id")
    private Long posterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;  // 등록자

    @OneToMany(mappedBy = "poster", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PosterImage> posterImages = new ArrayList<>();  // 포스터 이미지 - 최대 10장
    private String institution;  // 주최 팀 or 주최자 명
    @Column(name = "manager", nullable = false, length = 20)
    private String manager;  // 담당자 이름
    @OneToMany(mappedBy = "poster", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<PosterManager> posterManagers = new ArrayList<>();  // 임시 담당자 리스트
    @Column(name = "manager_contact", nullable = false, length = 20)
    private String managerContact;  // 담당자 연락처
    @Column(name = "manager_code")
    private String managerCode;  // 고유 식별 코드

    @OneToOne(mappedBy = "poster", cascade = CascadeType.ALL)
    private Event event;  // 등록할 이벤트

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;  // 카테고리

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "banner_id")
    private Banner banner;  // 배너

    @Column(name = "created_at")
    private LocalDateTime createdAt;  // 생성일자
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;  // 수정일자

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;  // 포스터 상태

    @Column(name = "reason")
    private String reason;  // 포스터 반려 이유
    @Column(name = "like_count")
    private int likeCount;  // 관심 등록 수

    // 생성 메서드
    public static Poster createPoster(Member member, WritePosterRequest request) {
        Poster poster = new Poster();

        poster.member = member;

        for (String posterImgUrl : request.getPosterImgUrls()) {
            PosterImage posterImage = PosterImage.createPosterImage(poster, posterImgUrl);
            poster.posterImages.add(posterImage);
        }

        poster.institution = request.getInstitution();
        poster.manager = request.getManager();
        poster.managerContact = request.getManagerContact();
        poster.managerCode = UUIDGenerator.generateNumericString(6);

        LocalDateTime currentTime = LocalDateTime.now();
        poster.createdAt = currentTime;
        poster.updatedAt = currentTime;

        poster.status = Status.WAIT;  // 초기에는 WAIT 상태로, 이후에 관리자의 승인을 받아야 ACTIVE로 전환됨
        poster.likeCount = 0;

        return poster;
    }

    // 업데이트 메서드
    public static Poster updatePoster(Poster poster, ModifyPosterRequest request, ImageUploadService imageUploadService) throws URISyntaxException {
        // 이미지에 수정된 것이 있는지 확인
        boolean isChangedImage = false;
        List<String> requestUrls = Arrays.asList(request.getPosterImgUrls());
        for (PosterImage posterImage : poster.posterImages) {
            String originalUrl = posterImage.getImageUrl();
            if (!requestUrls.contains(originalUrl)) {
                isChangedImage = true;
                break;
            }
        }

        // 이미지 수정이 필요한 경우
        if (isChangedImage) {
            // 기존의 포스터 이미지를 S3와 DB에서 삭제
            for (PosterImage posterImage : poster.posterImages) {
                log.info("posterURL={}", posterImage.getImageUrl());
                imageUploadService.deleteFile(posterImage.getImageUrl());
            }
            poster.posterImages.clear();

            // 전달된 이미지를 새롭게 추가
            for (String posterImgUrl : request.getPosterImgUrls()) {
                PosterImage posterImage = PosterImage.createPosterImage(poster, posterImgUrl);
                poster.posterImages.add(posterImage);
            }
        }

        poster.institution = request.getInstitution();
        poster.manager = request.getManager();
        poster.managerContact = request.getManagerContact();

        poster.event = Event.updateEvent(poster.getEvent(), request.getEvent(), imageUploadService);

        LocalDateTime currentTime = LocalDateTime.now();
        poster.updatedAt = currentTime;

        return poster;
    }

    // 삭제 메서드
    public static Poster deletePoster(Poster poster, ImageUploadService imageUploadService) throws URISyntaxException {
        // status를 DELETE로 변경
        poster.status = Status.DELETE;

        // S3에서 이미지 삭제
        for (PosterImage posterImage : poster.posterImages) {
            if (!posterImage.getImageUrl().isBlank()) {
                imageUploadService.deleteFile(posterImage.getImageUrl());
            }
        }
        // DB에서 이미지 삭제
        poster.posterImages.clear();

        // 연관된 담당자들의 managedPoster 연결 해제
        for (PosterManager posterManager : poster.getPosterManagers()) {
            Member.deletePosterManager(posterManager.getMember(), posterManager);
        }
        poster.posterManagers.clear();

        LocalDateTime currentTime = LocalDateTime.now();
        poster.updatedAt = currentTime;

        return poster;
    }

    // Event 설정
    public static Poster setEvent(Poster poster, Event event) {
        poster.event = event;

        return poster;
    }

    // 관리자 포스터 승인 메서드
    public static void approvePoster(Poster poster) {
        poster.status = Status.ACTIVE;  // WAIT -> ACTIVE로 전환

        LocalDateTime currentTime = LocalDateTime.now();
        poster.updatedAt = currentTime;
    }

    // 관리자 포스터 반려 메서드
    public static void rejectPoster(Poster poster, String reason) {
        poster.status = Status.REJECT;  // REJECT로 전환
        poster.reason = reason;

        LocalDateTime currentTime = LocalDateTime.now();
        poster.updatedAt = currentTime;
    }

    // 관심 등록 카운트 메서드
    public static void likePoster(Poster poster, int count) {
        if (poster.likeCount + count >= 0) {  // 0 밑으로 떨어지는 현상 방지
            poster.likeCount += count;
        }
    }

    // 이벤트 종료 메서드
    public static void endPoster(Poster poster) {
        poster.status = Status.END;

        LocalDateTime currentTime = LocalDateTime.now();
        poster.updatedAt = currentTime;
    }

    // 카테고리 설정 메서드
    public void setCategory(Category category) {
        this.category = category;
    }

    // 배너 설정 메서드
    public void setBanner(Banner banner) {
        this.banner = banner;
    }

    // 임시 담당자 추가
    public void addPosterManager(PosterManager posterManager) {
        this.posterManagers.add(posterManager);
    }
}
