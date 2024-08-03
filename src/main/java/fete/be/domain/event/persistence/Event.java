package fete.be.domain.event.persistence;

import com.fasterxml.jackson.annotation.JsonFormat;
import fete.be.domain.poster.application.dto.request.EventDto;
import fete.be.domain.poster.persistence.Poster;
import fete.be.domain.ticket.persistence.Participant;
import fete.be.domain.payment.persistence.Payment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Slf4j
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long eventId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type")
    private EventType eventType;  // 이벤트 종류 - FESTIVAL / PARTY

    @Column(name = "event_name")
    private String eventName;  // 이벤트 이름

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "start_date")
    private LocalDateTime startDate;  // 이벤트 시작일
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "end_date")
    private LocalDateTime endDate;  // 이벤트 종료일

    @Column(name = "address")
    private String address;  // 주소

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();

    @Column(name = "description", length = 300)
    private String description;  // 이벤트 관련 상세 설명
    @Column(name = "genre")
    @Enumerated(EnumType.STRING)
    private Genre genre;  // 이벤트 분위기

    @Column(name = "created_at")
    private LocalDateTime createdAt;  // 생성일자
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;  // 수정일자

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "poster_id")
    private Poster poster;  // 연결된 포스터

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants = new ArrayList<>();  // 이벤트 참여자 목록

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Payment> payments = new ArrayList<>();  // 결제 상태 리스트


    // 생성 메서드
    public static Event createEvent(Poster poster, EventDto request) {
        Event event = new Event();

        event.eventType = request.getEventType();
        event.startDate = request.getStartDate();
        event.endDate = request.getEndDate();
        event.address = request.getAddress();
        event.ticketName = request.getTicketName();
        event.ticketPrice = request.getTicketPrice();
        event.description = request.getDescription();
        event.genre = request.getGenre();

        LocalDateTime currentTime = LocalDateTime.now();
        event.createdAt = currentTime;
        event.updatedAt = currentTime;

        event.poster = poster;

        return event;
    }

    // 업데이트 메서드
    public static Event updateEvent(Event event, EventDto request) {
        event.eventType = request.getEventType();
        event.startDate = request.getStartDate();
        event.endDate = request.getEndDate();
        event.address = request.getAddress();
        event.ticketName = request.getTicketName();
        event.ticketPrice = request.getTicketPrice();
        event.description = request.getDescription();
        event.genre = request.getGenre();

        LocalDateTime currentTime = LocalDateTime.now();
        event.updatedAt = currentTime;

        return event;
    }
}
