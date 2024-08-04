package fete.be.domain.event.application.dto;

import fete.be.domain.payment.application.dto.request.TossPaymentRequest;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BuyTicketRequest {
    private String ticketType;  // 티켓 종류 - 얼리버드 / 현장구매 / 프로모션
    private int ticketPrice;  // 티켓 가격
    private int ticketNumber;  // 구매할 티켓 개수
    private TossPaymentRequest tossPaymentRequest;
}
