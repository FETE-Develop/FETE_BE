package fete.be.domain.payment.application.dto.request;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TossCancelRequest {
    private String cancelReason;  // 취소 사유
    private int cancelAmount;  // 부분 취소 금액
}
