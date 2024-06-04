package fete.be.domain.member.application.dto.request;

import lombok.Getter;

@Getter
public class SignupRequestDto {
    private String email;
    private String password;
    private String userName;
}
