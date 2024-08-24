package fete.be.domain.poster.application.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import fete.be.domain.admin.application.dto.response.AccountDto;
import fete.be.domain.event.persistence.ArtistDto;
import fete.be.domain.event.persistence.Genre;
import fete.be.domain.event.persistence.TicketInfoDto;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
public class EventDto {
    private String eventName;  // 이벤트 이름
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime startDate;  // 이벤트 시작일
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime endDate;  // 이벤트 종료일
    private Place place;   // 위치 정보
    private List<TicketInfoDto> tickets;  // 티켓 종류 및 가격
    private String description;  // 이벤트 관련 상세 설명
    private Genre genre;  // 이벤트 장르
    private String homepageUrl;  // 이벤트 관련 홈페이지 주소
    private List<ArtistDto> artists;  // 이벤트 라인업
    private AccountDto account;  // 이벤트 담당자의 계좌 정보
}
