package fete.be.domain.poster.persistence;

import fete.be.domain.event.persistence.Event;
import fete.be.domain.member.persistence.Member;
import fete.be.domain.poster.application.dto.request.ModifyPosterRequest;
import fete.be.domain.poster.application.dto.request.WritePosterRequest;
import fete.be.global.util.Status;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


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

    private String title;  // 포스터 제목
    private String posterImgUrl;  // 포스터 이미지
    private String institution;  // 기관명
    private String manager;  // 담당자
    private String managerContact;  // 담당자 연락처
    private String ticketName;  // 티켓 이름
    private int ticketPrice;  // 티켓 가격

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "event_id")
    private Event event;  // 등록할 이벤트

    private String createdAt;  // 생성일자
    private String updatedAt;  // 수정일자

    @Enumerated(EnumType.STRING)
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
        poster.ticketName = request.getTicketName();
        poster.ticketPrice = request.getTicketPrice();

        poster.event = Event.createEvent(request.getEvent());

        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
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
        poster.ticketName = request.getTicketName();
        poster.ticketPrice = request.getTicketPrice();

        poster.event = Event.updateEvent(poster.event, request.getEvent());

        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        poster.updatedAt = currentTime;

        return poster;
    }

    // 삭제 메서드
    public static Poster deletePoster(Poster poster) {
        // status를 DELETE로 변경
        poster.status = Status.DELETE;

        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        poster.updatedAt = currentTime;

        return poster;
    }

    // 관리자 승인 메서드
    public static void approvePoster(Poster poster) {
        poster.status = Status.ACTIVE;  // WAIT -> ACTIVE로 전환
    }
}
