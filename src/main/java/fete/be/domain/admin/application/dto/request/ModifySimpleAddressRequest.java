package fete.be.domain.admin.application.dto.request;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ModifySimpleAddressRequest {
    private String simpleAddress;  // 간단 주소
}
