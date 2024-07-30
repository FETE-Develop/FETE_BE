package fete.be.domain.member.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class LoginRequestDto {
    private String email;
    @JsonProperty("password")
    private String password;
}
