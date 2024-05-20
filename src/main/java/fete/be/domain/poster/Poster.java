package fete.be.domain.poster;

import fete.be.domain.participant.Participant;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Poster {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "poster_id")
    private Long posterId;

    private EventType eventType;  // 이벤트 종류 - FESTIVAL / PARTY
    private String title;  // 포스터 제목
    private String institution;  // 기관명
    private String manager;  // 담당자
    private String managerContact;  // 담당자 연락처
    private String homepage;  // 관련 홈페이지 주소
    private String startDate;  // 이벤트 시작일
    private String endDate;  // 이벤트 종료일
    private String address;  // 주소
    private String ticketSite;  // 티켓팅 사이트 URL
    private String ticketName;  // 티켓 이름
    private String ticketPrice;  // 티켓 가격
    private String detailInfo;  // 이벤트 관련 상세 설명
    private String posterImgUrl;  // 포스터 이미지

    @OneToMany(mappedBy = "poster", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants = new ArrayList<>();  // 참여자 목록

}
