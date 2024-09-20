package fete.be.domain.member.application.dto.request;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class FindIdRequest {
    private String phoneNumber;  // 휴대전화 번호
}
