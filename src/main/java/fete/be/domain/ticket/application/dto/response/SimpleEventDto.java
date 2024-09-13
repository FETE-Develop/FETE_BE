package fete.be.domain.ticket.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import fete.be.domain.event.persistence.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SimpleEventDto {
    private String eventName;
    private String posterImage;  // 대표 이미지 1장
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime endDate;
    private String address;

    public SimpleEventDto(Event event) {
        this.eventName = event.getEventName();
        this.posterImage = event.getPoster().getPosterImages().get(0).getImageUrl();
        this.startDate = event.getStartDate();
        this.endDate = event.getEndDate();
        this.address = event.getAddress();
    }
}
