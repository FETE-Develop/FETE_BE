package fete.be.domain.payment.persistence;


import fete.be.domain.payment.exception.NotFoundTicketStatusException;

public enum TicketStatus {
    UNUSED("UNUSED"),  // 사용 전
    COMPLETE("COMPLETE"),  // 사용 완료
    CANCEL("CANCEL"),  // 취소/환불
    ;


    private String ticketStatus;

    TicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public String getTicketStatus() {
        return this.ticketStatus;
    }

    public static TicketStatus convertTicketStatus(String ticketStatus) {
        for (TicketStatus status : TicketStatus.values()) {
            if (status.getTicketStatus().equals(ticketStatus)) {
                return status;
            }
        }
        throw new NotFoundTicketStatusException("일치하는 티켓 상태가 없습니다 : " + ticketStatus);
    }
}
