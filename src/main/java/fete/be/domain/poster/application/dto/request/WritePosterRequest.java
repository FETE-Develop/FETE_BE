package fete.be.domain.poster.application.dto.request;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class WritePosterRequest {
    private String[] posterImgUrls;  // 포스터 이미지
    private String institution;  // 기관명
    private String manager;  // 담당자
    private String managerContact;  // 담당자 연락처
    private EventDto event;  // 등록할 이벤트
}
