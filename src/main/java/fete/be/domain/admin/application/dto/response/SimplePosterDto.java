package fete.be.domain.admin.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import fete.be.domain.event.persistence.Genre;
import fete.be.domain.poster.persistence.Poster;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SimplePosterDto {
    private Long posterId;  // 포스터 아이디
    private String title;  // 포스터 제목
    private String posterImage;  // 대표 이미지 1장
    private String manager;  // 담당자 이름
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime startDate;  // 이벤트 시작일
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime endDate;  // 이벤트 종료일
    private String address;  // 주소
    private Genre genre;  // 장르

    public SimplePosterDto(Poster poster) {
        this.posterId = poster.getPosterId();
        this.title = poster.getTitle();
        this.posterImage = poster.getPosterImages().get(0).getImageUrl();
        this.manager = poster.getManager();
        this.startDate = poster.getEvent().getStartDate();
        this.endDate = poster.getEvent().getEndDate();
        this.address = poster.getEvent().getAddress();
        this.genre = poster.getEvent().getGenre();
    }
}
