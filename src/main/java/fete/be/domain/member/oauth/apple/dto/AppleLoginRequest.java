package fete.be.domain.member.oauth.apple.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class AppleLoginRequest {
    private String idToken;  // 애플 응답 - idToken
}
