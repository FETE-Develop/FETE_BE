package fete.be.domain.notification.application.dto.request;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class StoreTokenRequest {
    private String token;  // 프론트가 전달하는 FCM 토큰
}
