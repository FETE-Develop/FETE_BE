package fete.be.domain.member.application.dto.request;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class GrantAdminRequestDto {
    private String securityCode;
}
