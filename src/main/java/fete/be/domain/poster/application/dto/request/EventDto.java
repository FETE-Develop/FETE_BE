package fete.be.domain.poster.application.dto.request;

import fete.be.domain.event.persistence.EventType;
import lombok.Getter;

@Getter
public class EventDto {
    private EventType eventType;  // 이벤트 종류 - FESTIVAL / PARTY
    private String startDate;  // 이벤트 시작일
    private String endDate;  // 이벤트 종료일
    private String address;  // 주소
    private String ticketName;  // 티켓 이름
    private int ticketPrice;  // 티켓 가격
    private String description;  // 이벤트 관련 상세 설명
    private String mood;  // 이벤트 분위기
}
