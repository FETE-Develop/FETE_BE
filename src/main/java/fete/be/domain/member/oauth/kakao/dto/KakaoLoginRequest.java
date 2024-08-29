package fete.be.domain.member.oauth.kakao.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class KakaoLoginRequest {
    private String accessToken;  // 카카오 응답 - accessToken
    private String refreshToken;  // 카카오 응답 - refreshToken
}
