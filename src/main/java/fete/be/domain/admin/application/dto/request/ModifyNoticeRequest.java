package fete.be.domain.admin.application.dto.request;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ModifyNoticeRequest {
    private String title;
    private String content;
}
