package fete.be.domain.member.application.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CheckJwtTokenRequest {
    private String token;
}
