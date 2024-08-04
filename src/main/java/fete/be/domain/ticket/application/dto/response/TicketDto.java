package fete.be.domain.ticket.application.dto.response;

import fete.be.domain.event.persistence.EventType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TicketDto {
    private Long participantId;
    private EventType eventType;
    private String eventName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String address;
    private String ticketType;
    private int ticketPrice;
}
