package fete.be.domain.payment.application.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class TossPaymentRequest {
    private String paymentKey;
    private String orderId;
    private int amount;
}
