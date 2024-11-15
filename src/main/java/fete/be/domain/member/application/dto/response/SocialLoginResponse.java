package fete.be.domain.member.application.dto.response;

import fete.be.global.jwt.JwtToken;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SocialLoginResponse {
    private boolean isSignedUp;  // 기존 회원: true, 신규 회원: false
    private JwtToken token;
}
