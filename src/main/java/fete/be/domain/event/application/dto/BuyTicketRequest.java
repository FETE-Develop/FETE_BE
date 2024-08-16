package fete.be.domain.event.application.dto;

import fete.be.domain.payment.application.dto.request.TossPaymentRequest;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class BuyTicketRequest {
    private List<BuyTicketDto> tickets;
    private TossPaymentRequest tossPaymentRequest;
}
