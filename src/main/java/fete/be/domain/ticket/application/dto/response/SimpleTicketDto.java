package fete.be.domain.ticket.application.dto.response;

import fete.be.domain.payment.persistence.Payment;
import fete.be.domain.payment.persistence.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleTicketDto {
    private Long participantId;
    private String ticketType;
    private int ticketPrice;
    private Boolean isPaid;  // 결제 상태 : 지불 = true, 미지불 = false
    private String qrCode;  // QR 코드
    private Boolean isParticipated;  // 티켓 사용 여부
    private String ticketStatus;  // 티켓 상태

    public SimpleTicketDto(Payment payment, String qrCode) {
        this.participantId = payment.getParticipant().getParticipantId();
        this.ticketType = payment.getTicketType();
        this.ticketPrice = payment.getTicketPrice();
        this.isPaid = payment.getIsPaid();
        this.qrCode = qrCode;
        this.isParticipated = payment.getParticipant().getIsParticipated();
        this.ticketStatus = payment.getTicketStatus();
    }
}
