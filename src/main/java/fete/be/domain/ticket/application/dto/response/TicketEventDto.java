package fete.be.domain.ticket.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import fete.be.domain.event.persistence.Event;
import fete.be.domain.payment.persistence.Payment;
import fete.be.domain.payment.persistence.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
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
    private String ticketStatus;  // 티켓 상태

    public TicketEventDto(Event event, Payment payment, String paymentCode, String ticketStatus) {
        this.eventId = event.getEventId();
        this.eventName = event.getEventName();

        // 대표 이미지가 삭제된 경우
        if (event.getPoster().getPosterImages().size() == 0) {
            this.posterImage = "";
        } else {
            this.posterImage = event.getPoster().getPosterImages().get(0).getImageUrl();
        }

        this.startDate = event.getStartDate();
        this.endDate = event.getEndDate();
        this.paymentCode = paymentCode;
        this.isPaid = payment.getIsPaid();
        this.paymentAt = payment.getPaymentAt();
        this.canceledAt = payment.getCanceledAt();
        this.ticketStatus = ticketStatus;
    }
}
