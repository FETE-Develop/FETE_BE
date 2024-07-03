package fete.be.domain.member.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class LoginRequestDto {
    private String email;
    @JsonProperty("password")
    private String password;
}
