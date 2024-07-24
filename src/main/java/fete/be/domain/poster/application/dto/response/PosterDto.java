package fete.be.domain.poster.application.dto.response;

import fete.be.domain.event.persistence.EventType;
import fete.be.domain.poster.persistence.PosterImage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class PosterDto {
    private Long posterId;  // 포스터 아이디
    private String title;  // 포스터 제목
    private List<String> posterImages;  // 포스터 이미지
    private String institution;  // 기관명

    private EventType eventType;  // 이벤트 종류 - FESTIVAL / PARTY
    private LocalDateTime startDate;  // 이벤트 시작일
    private LocalDateTime endDate;  // 이벤트 종료일
    private String address;  // 주소
    private String ticketName;  // 티켓 이름
    private int ticketPrice;  // 티켓 가격
    private String description;  // 이벤트 관련 상세 설명
    private String mood;  // 이벤트 분위기

    private Boolean isLike;  // 사용자의 관심 등록 상태
}
