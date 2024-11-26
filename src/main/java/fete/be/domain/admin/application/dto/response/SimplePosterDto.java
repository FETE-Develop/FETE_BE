package fete.be.domain.admin.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import fete.be.domain.event.persistence.Genre;
import fete.be.domain.event.persistence.Mood;
import fete.be.domain.poster.persistence.Poster;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SimplePosterDto {
    private Long posterId;  // 포스터 아이디
    private String eventName;  // 이벤트 이름
    private String posterImage;  // 대표 이미지 1장
    private String manager;  // 담당자 이름
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;  // 이벤트 시작일
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;  // 이벤트 종료일
    private String address;  // 주소
    private String simpleAddress;  // 간단 주소
    private String moods;  // 이벤트 무드
    private String genres;  // 장르
    private Boolean isLike;  // 사용자의 관심 등록 상태

    public SimplePosterDto(Poster poster, Boolean isLike) {
        this.posterId = poster.getPosterId();
        this.eventName = poster.getEvent().getEventName();

        // 대표 이미지가 삭제된 경우
        if (poster.getPosterImages().size() == 0) {
            this.posterImage = "";
        } else {
            this.posterImage = poster.getPosterImages().get(0).getImageUrl();
        }

        this.manager = poster.getManager();
        this.startDate = poster.getEvent().getStartDate();
        this.endDate = poster.getEvent().getEndDate();
        this.address = poster.getEvent().getAddress();
        this.simpleAddress = poster.getEvent().getSimpleAddress();
        this.moods = poster.getEvent().getMoods();
        this.genres = poster.getEvent().getGenres();
        this.isLike = isLike;
    }
}
