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
    @Column(name = "simple_address")
    private String simpleAddress;  // 간단 주소
    @Column(name = "detail_address")
    private String detailAddress;  // 상세 주소
    @Column(name = "latitude")
    private double latitude;  // 위도
    @Column(name = "longitude")
    private double longitude;  // 경도

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();  // 티켓 종류 및 가격 정보

    @Column(name = "description", length = 1000)
    private String description;  // 이벤트 관련 상세 설명

    @Column(name = "mood")
    @Enumerated(EnumType.STRING)
    private Mood mood;  // 이벤트 무드

    @Column(name = "genre")
    @Enumerated(EnumType.STRING)
    private Genre genre;  // 이벤트 장르

    @Column(name = "homepage_url")
    private String homepageUrl;  // 이벤트 관련 홈페이지 주소

    @Column(name = "artists")
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Artist> artists = new ArrayList<>();  // 이벤트 라인업

    @Column(name = "total_profit")
    private int totalProfit = 0;  // 총 수익
    @Column(name = "bank_name")
    private String bankName;  // 은행
    @Column(name = "account_number")
    private String accountNumber;  // 계좌번호
    @Column(name = "account_holder")
    private String accountHolder;  // 예금주

    @Column(name = "created_at")
    private LocalDateTime createdAt;  // 생성일자
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;  // 수정일자

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "poster_id")
    private Poster poster;  // 연결된 포스터

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants = new ArrayList<>();  // 이벤트 참여자 목록

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();  // 결제 상태 리스트


    // 생성 메서드
    public static Event createEvent(Poster poster, EventDto request) {
        Event event = new Event();

        event.eventName = request.getEventName();
        event.startDate = request.getStartDate();
        event.endDate = request.getEndDate();

        event.address = request.getPlace().getAddress();
        event.simpleAddress = request.getPlace().getSimpleAddress();
        event.detailAddress = request.getPlace().getDetailAddress();
        event.latitude = request.getPlace().getLatitude();
        event.longitude = request.getPlace().getLongitude();

        // 티켓 종류 및 가격 생성
        for (TicketInfoDto ticketInfoDto : request.getTickets()) {
            Ticket ticket = Ticket.createTicket(ticketInfoDto, event);
            event.tickets.add(ticket);
        }

        event.description = request.getDescription();
        event.mood = Mood.convertMoodEnum(request.getMood());
        event.genre = request.getGenre();
        event.homepageUrl = request.getHomepageUrl();

        // 라인업 생성
        for (ArtistDto artistDto : request.getArtists()) {
            Artist artist = Artist.createArtist(artistDto, event);
            event.artists.add(artist);
        }

        // 계좌 정보
        event.bankName = request.getAccount().getBankName();
        event.accountNumber = request.getAccount().getAccountNumber();
        event.accountHolder = request.getAccount().getAccountHolder();

        LocalDateTime currentTime = LocalDateTime.now();
        event.createdAt = currentTime;
        event.updatedAt = currentTime;

        event.poster = poster;

        return event;
    }

    // 업데이트 메서드
    public static Event updateEvent(Event event, EventDto request) {
        event.eventName = request.getEventName();
        event.startDate = request.getStartDate();
        event.endDate = request.getEndDate();

        event.address = request.getPlace().getAddress();
        event.simpleAddress = request.getPlace().getSimpleAddress();
        event.detailAddress = request.getPlace().getDetailAddress();
        event.latitude = request.getPlace().getLatitude();
        event.longitude = request.getPlace().getLongitude();

        event.description = request.getDescription();
        event.mood = Mood.convertMoodEnum(request.getMood());
        event.genre = request.getGenre();
        event.homepageUrl = request.getHomepageUrl();

        // 라인업 생성
        event.artists.clear();
        for (ArtistDto artistDto : request.getArtists()) {
            Artist artist = Artist.createArtist(artistDto, event);
            event.artists.add(artist);
        }

        // 계좌 정보
        event.bankName = request.getAccount().getBankName();
        event.accountNumber = request.getAccount().getAccountNumber();
        event.accountHolder = request.getAccount().getAccountHolder();

        LocalDateTime currentTime = LocalDateTime.now();
        event.updatedAt = currentTime;

        return event;
    }

    // 결제 금액 총 수익에 반영하는 메서드
    public static void updateTotalProfit(Event event, int amount) {
        event.totalProfit += amount;

        LocalDateTime currentTime = LocalDateTime.now();
        event.updatedAt = currentTime;
    }

    // 취소 금액을 수익에 반영하는 메서드
    public void updateProfit(int amount) {
        this.totalProfit += amount;

        LocalDateTime currentTime = LocalDateTime.now();
        this.updatedAt = currentTime;
    }

    // Participant 양방향 매핑
    public static void setParticipants(Participant participant, Event event) {
        event.participants.add(participant);

        LocalDateTime currentTime = LocalDateTime.now();
        event.updatedAt = currentTime;
    }
}
