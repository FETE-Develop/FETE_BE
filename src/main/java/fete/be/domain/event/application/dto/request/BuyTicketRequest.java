package fete.be.domain.event.application.dto.request;

import fete.be.domain.payment.application.dto.request.TossPaymentRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
public class BuyTicketRequest {
    private List<BuyTicketDto> tickets;
    private TossPaymentRequest tossPaymentRequest;
}
