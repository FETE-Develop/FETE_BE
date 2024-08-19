package fete.be.domain.member.application.dto.response;

import fete.be.global.jwt.JwtToken;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SocialLoginResponse {
    private boolean isSignedUp;  // 기존 회원: true, 신규 회원: false
    private JwtToken token;
}
