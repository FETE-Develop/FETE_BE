package fete.be.domain.poster.persistence;

import fete.be.domain.event.persistence.Event;
import fete.be.domain.member.persistence.Member;
import fete.be.domain.poster.application.dto.request.ModifyPosterRequest;
import fete.be.domain.poster.application.dto.request.WritePosterRequest;
import fete.be.global.util.Status;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;


@Entity
@Getter
public class Poster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "poster_id")
    private Long posterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "title", nullable = false, length = 50)
    private String title;  // 포스터 제목
    private String posterImgUrl;  // 포스터 이미지
    private String institution;  // 주최 팀 or 주최자 명
    @Column(name = "manager", nullable = false, length = 20)
    private String manager;  // 담당자 이름
    @Column(name = "manager_contact", nullable = false, length = 20)
    private String managerContact;  // 담당자 연락처

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "event_id")
    private Event event;  // 등록할 이벤트

    @Column(name = "created_at")
    private LocalDateTime createdAt;  // 생성일자
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;  // 수정일자

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;  // 포스터 상태


    // 생성 메서드
    public static Poster createPoster(Member member, WritePosterRequest request) {
        Poster poster = new Poster();

        poster.member = member;
        poster.title = request.getTitle();
        poster.posterImgUrl = request.getPosterImgUrl();
        poster.institution = request.getInstitution();
        poster.manager = request.getManager();
        poster.managerContact = request.getManagerContact();

        poster.event = Event.createEvent(request.getEvent());

//        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime currentTime = LocalDateTime.now();
        poster.createdAt = currentTime;
        poster.updatedAt = currentTime;

        poster.status = Status.WAIT;  // 초기에는 WAIT 상태로, 이후에 관리자의 승인을 받아야 ACTIVE로 전환됨

        return poster;
    }

    // 업데이트 메서드
    public static Poster updatePoster(Poster poster, ModifyPosterRequest request) {
        poster.title = request.getTitle();
        poster.posterImgUrl = request.getPosterImgUrl();
        poster.institution = request.getInstitution();
        poster.manager = request.getManager();
        poster.managerContact = request.getManagerContact();

        poster.event = Event.updateEvent(poster.event, request.getEvent());

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

    // 관리자 승인 메서드
    public static void approvePoster(Poster poster) {
        poster.status = Status.ACTIVE;  // WAIT -> ACTIVE로 전환
    }
}
