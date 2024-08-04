package fete.be.domain.poster.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import fete.be.domain.event.persistence.EventType;
import fete.be.domain.event.persistence.Genre;
import fete.be.domain.event.persistence.TicketInfoDto;
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
    private String eventName;  // 이벤트 이름

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime startDate;  // 이벤트 시작일
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime endDate;  // 이벤트 종료일

    private String address;  // 주소
    private List<TicketInfoDto> tickets;  // 티켓 종류 및 가격
    private String description;  // 이벤트 관련 상세 설명
    private Genre genre;  // 장르

    private Boolean isLike;  // 사용자의 관심 등록 상태
    private int likeCount;  // 포스터의 관심 등록 수
}
