package fete.be.domain.poster.application.dto.request;

import fete.be.domain.event.EventDto;

import lombok.Getter;

@Getter
public class WritePosterRequest {
    private String title;  // 포스터 제목
    private String posterImgUrl;  // 포스터 이미지
    private String institution;  // 기관명
    private String manager;  // 담당자
    private String managerContact;  // 담당자 연락처
    private String ticketName;  // 티켓 이름
    private int ticketPrice;  // 티켓 가격
    private EventDto event;  // 등록할 이벤트
}
