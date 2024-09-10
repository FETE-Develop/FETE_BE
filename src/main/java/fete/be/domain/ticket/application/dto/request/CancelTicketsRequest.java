package fete.be.domain.ticket.application.dto.request;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class CancelTicketsRequest {
    private List<Long> ticketIds;  // 취소할 participantId 리스트
    private String cancelReason;  // 토스 결제 취소 사유
}
