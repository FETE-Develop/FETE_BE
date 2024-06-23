package fete.be.domain.event;

import fete.be.domain.payment.Payment;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Event {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long eventId;

    private EventType eventType;  // 이벤트 종류 - FESTIVAL / PARTY
    private String startDate;  // 이벤트 시작일
    private String endDate;  // 이벤트 종료일
    private String address;  // 주소
    private String description;  // 이벤트 관련 상세 설명
    private String mood;  // 이벤트 분위기
    private String createdAt;  // 생성일자
    private String updatedAt;  // 수정일자

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants = new ArrayList<>();  // 이벤트 참여자 목록

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Payment> payments = new ArrayList<>();  // 결제 상태 리스트


    // 생성 메서드
    public static Event createEvent(EventDto request) {
        Event event = new Event();

        event.eventType = request.getEventType();
        event.startDate = request.getStartDate();
        event.endDate = request.getEndDate();
        event.address = request.getAddress();
        event.description = request.getDescription();
        event.mood = request.getMood();

        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        event.createdAt = currentTime;
        event.updatedAt = currentTime;

        return event;
    }
}
