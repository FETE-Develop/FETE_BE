package fete.be.domain.event.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetManagerCodeResponse {
    private String managerCode;  // 고유 식별 코드
}
