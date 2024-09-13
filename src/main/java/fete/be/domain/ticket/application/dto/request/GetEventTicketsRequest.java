package fete.be.domain.ticket.application.dto.request;

import lombok.Getter;

@Getter
public class GetEventTicketsRequest {
    private Long eventId;
    private String paymentCode;
}
