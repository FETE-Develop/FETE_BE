package fete.be.domain.member.application.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ModifyPasswordRequest {
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16 자리의 영문, 숫자, 특수문자로 이루어져야 합니다.")
    private String password;
}
