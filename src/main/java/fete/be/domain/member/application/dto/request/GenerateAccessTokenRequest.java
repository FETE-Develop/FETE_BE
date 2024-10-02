package fete.be.domain.member.application.dto.request;

import lombok.Getter;

@Getter
public class GenerateAccessTokenRequest {
    private String refreshToken;
}
