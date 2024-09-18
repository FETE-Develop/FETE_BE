package fete.be.domain.poster.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import fete.be.domain.admin.application.dto.response.AccountDto;
import fete.be.domain.event.persistence.ArtistDto;
import fete.be.domain.event.persistence.Genre;
import fete.be.domain.event.persistence.TicketInfoDto;
import fete.be.domain.poster.application.dto.request.Place;
import fete.be.domain.poster.persistence.Poster;
import fete.be.domain.poster.persistence.PosterImage;
import fete.be.global.util.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class PosterDto {
    private Long posterId;  // 포스터 아이디
    private String title;  // 포스터 제목
    private List<String> posterImages;  // 포스터 이미지
    private String institution;  // 기관명

    private String eventName;  // 이벤트 이름
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime startDate;  // 이벤트 시작일
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime endDate;  // 이벤트 종료일

    private Place place;  // 위치 정보
    private List<TicketInfoDto> tickets;  // 티켓 종류 및 가격
    private String description;  // 이벤트 관련 상세 설명
    private String mood;  // 무드
    private Genre genre;  // 장르
    private String homepageUrl;  // 이벤트 관련 홈페이지 주소
    private List<ArtistDto> artists;  // 이벤트 라인업

    private Boolean isLike;  // 사용자의 관심 등록 상태
    private int likeCount;  // 포스터의 관심 등록 수
    private Status status;  // 포스터 상태

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private AccountDto account;  // 결제 정보

    public PosterDto(Poster poster, Boolean isLike) {
        this.posterId = poster.getPosterId();
        this.title = poster.getTitle();

        this.posterImages = poster.getPosterImages().stream()
                .map(PosterImage::getImageUrl)
                .collect(Collectors.toList());

        this.institution = poster.getInstitution();
        this.eventName = poster.getEvent().getEventName();
        this.startDate = poster.getEvent().getStartDate();
        this.endDate = poster.getEvent().getEndDate();
        this.place = new Place(poster.getEvent().getAddress(), poster.getEvent().getSimpleAddress(),
                poster.getEvent().getLatitude(), poster.getEvent().getLongitude());

        this.tickets = poster.getEvent().getTickets().stream()
                .map(ticket -> new TicketInfoDto(
                        ticket.getTicketType(),
                        ticket.getDescription(),
                        ticket.getTicketPrice(),
                        ticket.getMaxTicketCount()
                ))
                .collect(Collectors.toList());

        this.description = poster.getEvent().getDescription();
        this.mood = poster.getEvent().getMood().getKoreanValue();
        this.genre = poster.getEvent().getGenre();
        this.homepageUrl = poster.getEvent().getHomepageUrl();

        this.artists = poster.getEvent().getArtists().stream()
                .map(artist -> new ArtistDto(
                        artist.getName(),
                        artist.getInfoUrl(),
                        artist.getImageUrl()
                ))
                .collect(Collectors.toList());

        this.isLike = isLike;
        this.likeCount = poster.getLikeCount();
        this.status = poster.getStatus();
    }

    public PosterDto(Poster poster, Boolean isLike, AccountDto account) {
        this.posterId = poster.getPosterId();
        this.title = poster.getTitle();

        this.posterImages = poster.getPosterImages().stream()
                .map(PosterImage::getImageUrl)
                .collect(Collectors.toList());

        this.institution = poster.getInstitution();
        this.eventName = poster.getEvent().getEventName();
        this.startDate = poster.getEvent().getStartDate();
        this.endDate = poster.getEvent().getEndDate();
        this.place = new Place(poster.getEvent().getAddress(), poster.getEvent().getSimpleAddress(),
                poster.getEvent().getLatitude(), poster.getEvent().getLongitude());

        this.tickets = poster.getEvent().getTickets().stream()
                .map(ticket -> new TicketInfoDto(
                        ticket.getTicketType(),
                        ticket.getDescription(),
                        ticket.getTicketPrice(),
                        ticket.getMaxTicketCount()
                ))
                .collect(Collectors.toList());

        this.description = poster.getEvent().getDescription();
        this.mood = poster.getEvent().getMood().getKoreanValue();
        this.genre = poster.getEvent().getGenre();
        this.homepageUrl = poster.getEvent().getHomepageUrl();

        this.artists = poster.getEvent().getArtists().stream()
                .map(artist -> new ArtistDto(
                        artist.getName(),
                        artist.getInfoUrl(),
                        artist.getImageUrl()
                ))
                .collect(Collectors.toList());

        this.isLike = isLike;
        this.likeCount = poster.getLikeCount();
        this.status = poster.getStatus();

        this.account = account;
    }
}
