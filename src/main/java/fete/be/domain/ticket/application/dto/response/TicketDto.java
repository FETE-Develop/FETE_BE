package fete.be.domain.ticket.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import fete.be.domain.event.persistence.EventType;
import fete.be.domain.ticket.persistence.Participant;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TicketDto {
    private Long participantId;
    private String eventName;
    private String posterImage;  // 대표 이미지 1장
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime endDate;
    private String address;
    private String ticketType;
    private int ticketPrice;
    private Boolean isPaid;  // 결제 상태 : 지불 = true, 미지불 = false
    private int totalAmount;  // 총 결제 금액
    private String method;  // 결제 수단
    private String cancelReason;  // 취소 사유
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime paymentAt;  // 결제일자

    public TicketDto(Participant participant) {
        this.participantId = participant.getParticipantId();
        this.eventName = participant.getEvent().getEventName();
        this.posterImage = participant.getEvent().getPoster().getPosterImages().get(0).getImageUrl();
        this.startDate = participant.getEvent().getStartDate();
        this.endDate = participant.getEvent().getEndDate();
        this.address = participant.getEvent().getAddress();
        this.ticketType = participant.getPayment().getTicketType();
        this.ticketPrice = participant.getPayment().getTicketPrice();
        this.isPaid = participant.getPayment().getIsPaid();
        this.totalAmount = participant.getPayment().getTotalAmount();
        this.method = participant.getPayment().getMethod();
        this.cancelReason = participant.getPayment().getCancelReason();
        this.paymentAt = participant.getPayment().getPaymentAt();
    }
}
