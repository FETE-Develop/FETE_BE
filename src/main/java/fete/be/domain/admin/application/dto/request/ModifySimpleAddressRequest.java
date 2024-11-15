package fete.be.domain.admin.application.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class ModifySimpleAddressRequest {
    private String simpleAddress;  // 간단 주소
}
