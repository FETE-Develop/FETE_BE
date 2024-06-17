package fete.be.domain.member.application.dto.response;

import fete.be.global.jwt.JwtToken;
import lombok.Getter;

@Getter
public class LoginResponseDto {
    private JwtToken token;

    public LoginResponseDto(JwtToken token) {
        this.token = token;
    }
}
