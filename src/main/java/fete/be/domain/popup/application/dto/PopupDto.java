package fete.be.domain.popup.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class PopupDto {
    private Long popupId;  // 팝업 아이디
    private String imageUrl;  // 팝업 이미지
    private Long posterId;  // 연결할 포스터 Id
}
