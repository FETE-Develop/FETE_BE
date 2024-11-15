package fete.be.domain.email.application.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class VerifyEmailRequest {
    @Email
    private String email;  // 사용자의 이메일
    private String verifyCode;  // 사용자가 입력한 인증코드
}
