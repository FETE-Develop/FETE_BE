package fete.be.domain.event.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Entity
@Getter
@Slf4j
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long ticketId;

    @Column(name = "ticket_type")
    private String ticketType;  // 티켓 종류 - 얼리버드 / 현장구매 / 프로모션

    @Column(name = "ticket_price")
    private int ticketPrice;  // 티켓 가격

    @Column(name = "max_ticket_count")
    private int maxTicketCount;  // 티켓의 최대 개수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    public static Ticket createTicket(TicketInfoDto ticketInfoDto, Event event) {
        Ticket ticket = new Ticket();

        ticket.ticketType = ticketInfoDto.getTicketType();
        ticket.ticketPrice = ticketInfoDto.getTicketPrice();
        ticket.maxTicketCount = ticketInfoDto.getMaxTicketCount();
        ticket.event = event;

        return ticket;
    }
}
