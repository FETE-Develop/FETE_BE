package fete.be.domain.ticket.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetTicketInfoResponse {
    private TicketDto ticket;
    private String qrCode;
}
