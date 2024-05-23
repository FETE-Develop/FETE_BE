package fete.be.domain.poster;

import fete.be.domain.event.Event;
import jakarta.persistence.*;
import lombok.Getter;


@Entity
@Getter
public class Poster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "poster_id")
    private Long posterId;

    private String title;  // 포스터 제목
    private String posterImgUrl;  // 포스터 이미지
    private String institution;  // 기관명
    private String manager;  // 담당자
    private String managerContact;  // 담당자 연락처
    private String ticketName;  // 티켓 이름
    private String ticketPrice;  // 티켓 가격

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "event_id")
    private Event event;  // 등록할 이벤트

    private String createdAt;  // 생성일자
    private String updatedAt;  // 수정일자
}
