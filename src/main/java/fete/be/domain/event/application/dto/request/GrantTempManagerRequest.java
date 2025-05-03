package fete.be.domain.event.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GrantTempManagerRequest {
    private String managerCode;  // 고유 식별 코드
}
