package fete.be.domain.ticket.application.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetEventTicketsRequest {
    private Long eventId;
    private String paymentCode;
}
