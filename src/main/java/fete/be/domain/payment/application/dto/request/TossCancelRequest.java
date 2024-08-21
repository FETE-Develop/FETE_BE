package fete.be.domain.payment.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class TossCancelRequest {
    private String cancelReason;
}
