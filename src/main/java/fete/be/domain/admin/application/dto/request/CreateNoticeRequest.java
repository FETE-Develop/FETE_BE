package fete.be.domain.admin.application.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class CreateNoticeRequest {
    private String title;  // 배너 제목
    private String content;  // 배너 내용
}
