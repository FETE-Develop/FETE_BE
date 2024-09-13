package fete.be.domain.ticket.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import fete.be.domain.event.persistence.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TicketEventDto {
    private Long eventId;
    private String eventName;
    private String posterImage;  // 대표 이미지 1장
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime endDate;
    private String paymentCode;  // 결제번호

    public TicketEventDto(Event event, String paymentCode) {
        this.eventId = event.getEventId();
        this.eventName = event.getEventName();
        this.posterImage = event.getPoster().getPosterImages().get(0).getImageUrl();
        this.startDate = event.getStartDate();
        this.endDate = event.getEndDate();
        this.paymentCode = paymentCode;
    }
}
