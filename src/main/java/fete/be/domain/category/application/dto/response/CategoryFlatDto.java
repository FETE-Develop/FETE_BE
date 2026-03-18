package fete.be.domain.category.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CategoryFlatDto {
    private Long categoryId;
    private String categoryName;

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
}
