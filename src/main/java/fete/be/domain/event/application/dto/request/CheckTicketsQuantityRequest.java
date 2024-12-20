package fete.be.domain.event.application.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
public class CheckTicketsQuantityRequest {
    private List<BuyTicketDto> tickets;
}
