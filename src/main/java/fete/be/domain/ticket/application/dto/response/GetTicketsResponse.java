package fete.be.domain.ticket.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetTicketsResponse {
    List<TicketDto> tickets;
}
