package fete.be.domain.ticket.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import fete.be.domain.event.persistence.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleEventDto {
    private String eventName;
    private String posterImage;  // 대표 이미지 1장
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;
    private String address;

    public SimpleEventDto(Event event) {
        this.eventName = event.getEventName();

        // 대표 이미지가 삭제된 경우
        if (event.getPoster().getPosterImages().size() == 0) {
            this.posterImage = "";
        } else {
            this.posterImage = event.getPoster().getPosterImages().get(0).getImageUrl();
        }

        this.startDate = event.getStartDate();
        this.endDate = event.getEndDate();
        this.address = event.getAddress();
    }
}
