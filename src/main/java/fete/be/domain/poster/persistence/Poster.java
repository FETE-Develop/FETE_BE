package fete.be.domain.poster.persistence;

import fete.be.domain.category.persistence.Category;
import fete.be.domain.event.persistence.Event;
import fete.be.domain.member.persistence.Member;
import fete.be.domain.poster.application.dto.request.ModifyPosterRequest;
import fete.be.domain.poster.application.dto.request.WritePosterRequest;
import fete.be.global.util.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Column(name = "title", nullable = false, length = 30)
    private String title;  // 포스터 제목

    @OneToMany(mappedBy = "poster", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PosterImage> posterImages = new ArrayList<>();  // 포스터 이미지 - 최대 10장
    private String institution;  // 주최 팀 or 주최자 명
    @Column(name = "manager", nullable = false, length = 20)
    private String manager;  // 담당자 이름
    @Column(name = "manager_contact", nullable = false, length = 20)
    private String managerContact;  // 담당자 연락처

    @OneToOne(mappedBy = "poster", cascade = CascadeType.ALL)
    private Event event;  // 등록할 이벤트
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;  // 카테고리

    @Column(name = "created_at")
    private LocalDateTime createdAt;  // 생성일자
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;  // 수정일자

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;  // 포스터 상태

    private int likeCount;  // 관심 등록 수


    // 생성 메서드
    public static Poster createPoster(Member member, WritePosterRequest request) {
        Poster poster = new Poster();

        poster.member = member;
        poster.title = request.getTitle();

        for (String posterImgUrl : request.getPosterImgUrls()) {
            PosterImage posterImage = PosterImage.createPosterImage(poster, posterImgUrl);
            poster.posterImages.add(posterImage);
        }

        poster.institution = request.getInstitution();
        poster.manager = request.getManager();
        poster.managerContact = request.getManagerContact();

        LocalDateTime currentTime = LocalDateTime.now();
        poster.createdAt = currentTime;
        poster.updatedAt = currentTime;

        poster.status = Status.WAIT;  // 초기에는 WAIT 상태로, 이후에 관리자의 승인을 받아야 ACTIVE로 전환됨
        poster.likeCount = 0;

        return poster;
    }

    // 업데이트 메서드
    public static Poster updatePoster(Poster poster, ModifyPosterRequest request) {
        poster.title = request.getTitle();

        // 기존의 포스터 이미지를 삭제하고, 전달된 이미지를 추가
        poster.posterImages.clear();
        for (String posterImgUrl : request.getPosterImgUrls()) {
            PosterImage posterImage = PosterImage.createPosterImage(poster, posterImgUrl);
            poster.posterImages.add(posterImage);
        }

        poster.institution = request.getInstitution();
        poster.manager = request.getManager();
        poster.managerContact = request.getManagerContact();

        poster.event = Event.updateEvent(poster.getEvent(), request.getEvent());

        LocalDateTime currentTime = LocalDateTime.now();
        poster.updatedAt = currentTime;

        return poster;
    }

    // 삭제 메서드
    public static Poster deletePoster(Poster poster) {
        // status를 DELETE로 변경
        poster.status = Status.DELETE;

        LocalDateTime currentTime = LocalDateTime.now();
        poster.updatedAt = currentTime;

        return poster;
    }

    // Event 설정
    public static Poster setEvent(Poster poster, Event event) {
        poster.event = event;

        return poster;
    }

    // 관리자 승인 메서드
    public static void approvePoster(Poster poster) {
        poster.status = Status.ACTIVE;  // WAIT -> ACTIVE로 전환

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
}
