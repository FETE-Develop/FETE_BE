package fete.be.domain.member.application.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class GrantAdminRequestDto {
    private String securityCode;
}
