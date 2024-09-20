package fete.be.domain.member.application.dto.request;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class FindPasswordRequest {
    private String email;  // 이메일
}
