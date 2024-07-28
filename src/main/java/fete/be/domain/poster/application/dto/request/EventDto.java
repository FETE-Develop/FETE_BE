package fete.be.domain.poster.application.dto.request;

import fete.be.domain.event.persistence.EventType;
import fete.be.domain.event.persistence.Genre;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class EventDto {
    private EventType eventType;  // 이벤트 종류 - FESTIVAL / PARTY
    private LocalDateTime startDate;  // 이벤트 시작일
    private LocalDateTime endDate;  // 이벤트 종료일
    private String address;  // 주소
    private String ticketName;  // 티켓 이름
    private int ticketPrice;  // 티켓 가격
    private String description;  // 이벤트 관련 상세 설명
    private Genre genre;  // 장르
}
