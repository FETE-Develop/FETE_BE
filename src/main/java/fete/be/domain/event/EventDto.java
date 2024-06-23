package fete.be.domain.event;

import lombok.Getter;

@Getter
public class EventDto {
    private EventType eventType;  // 이벤트 종류 - FESTIVAL / PARTY
    private String startDate;  // 이벤트 시작일
    private String endDate;  // 이벤트 종료일
    private String address;  // 주소
    private String description;  // 이벤트 관련 상세 설명
    private String mood;  // 이벤트 분위기
}
