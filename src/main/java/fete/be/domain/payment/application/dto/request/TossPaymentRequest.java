package fete.be.domain.payment.application.dto.request;

import lombok.Getter;

@Getter
public class TossPaymentRequest {
    private String paymentKey;
    private String orderId;
    private int amount;
}
