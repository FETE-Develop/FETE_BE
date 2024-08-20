package fete.be.domain.ticket.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import fete.be.domain.event.persistence.EventType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TicketDto {
    private Long participantId;
    private String eventName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime endDate;
    private String address;
    private String ticketType;
    private int ticketPrice;
}
