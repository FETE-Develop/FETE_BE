package fete.be.domain.ticket.web;

import fete.be.domain.ticket.application.dto.response.GetTicketsResponse;
import fete.be.domain.ticket.application.dto.response.TicketDto;
import fete.be.domain.ticket.application.TicketService;
import fete.be.global.util.ApiResponse;
import fete.be.global.util.Logging;
import fete.be.global.util.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;


    /**
     * 구매한 티켓 조회 API
     *
     * @return ApiResponse<GetTicketsResponse>
     */
    @GetMapping
    public ApiResponse<GetTicketsResponse> getTickets() {
        log.info("GetTickets request");
        Logging.time();

        List<TicketDto> tickets = ticketService.getTickets();
        GetTicketsResponse result = new GetTicketsResponse(tickets);

        return new ApiResponse<>(ResponseMessage.TICKET_SUCCESS.getCode(), ResponseMessage.TICKET_SUCCESS.getMessage(), result);
    }
}
