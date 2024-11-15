package fete.be.domain.admin.application.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class ModifyPopupRequest {
    private String imageUrl;  // 팝업 이미지
    private Long posterId;  // 연결할 포스터 Id
}
