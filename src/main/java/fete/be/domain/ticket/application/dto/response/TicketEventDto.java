package fete.be.domain.ticket.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import fete.be.domain.event.persistence.Event;
import fete.be.domain.payment.persistence.Payment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TicketEventDto {
    private Long eventId;
    private String eventName;
    private String posterImage;  // 대표 이미지 1장
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;
    private String paymentCode;  // 결제번호
    private Boolean isPaid;  // 결제 상태 : 지불 = true, 미지불 = false
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentAt;  // 결제일자
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime canceledAt;  // 취소일자


    public TicketEventDto(Event event, Payment payment, String paymentCode) {
        this.eventId = event.getEventId();
        this.eventName = event.getEventName();
        this.posterImage = event.getPoster().getPosterImages().get(0).getImageUrl();
        this.startDate = event.getStartDate();
        this.endDate = event.getEndDate();
        this.paymentCode = paymentCode;
        this.isPaid = payment.getIsPaid();
        this.paymentAt = payment.getPaymentAt();
        this.canceledAt = payment.getCanceledAt();
    }
}
