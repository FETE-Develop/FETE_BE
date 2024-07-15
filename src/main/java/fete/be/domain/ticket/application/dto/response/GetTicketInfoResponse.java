package fete.be.domain.ticket.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetTicketInfoResponse {
    private TicketDto ticket;
    private String qrCode;
}
