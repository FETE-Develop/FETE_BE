package fete.be.domain.ticket.web;

import fete.be.domain.ticket.application.dto.response.GetTicketInfoResponse;
import fete.be.domain.ticket.application.dto.response.GetTicketsResponse;
import fete.be.domain.ticket.application.dto.response.TicketDto;
import fete.be.domain.ticket.application.TicketService;
import fete.be.global.util.ApiResponse;
import fete.be.global.util.Logging;
import fete.be.global.util.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    /**
     * 티켓 상세 정보 조회 API
     * - QR 코드, 이벤트 정보
     */
    @GetMapping("/{participantId}")
    public ApiResponse<GetTicketInfoResponse> getTicketInfo(@PathVariable("participantId") Long participantId) {
        log.info("GetTicketInfo request");
        Logging.time();

        try {
            GetTicketInfoResponse result = ticketService.getTicketInfo(participantId);
            return new ApiResponse<>(ResponseMessage.TICKET_SUCCESS.getCode(), ResponseMessage.TICKET_SUCCESS.getMessage(), result);
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.TICKET_NO_EXIST.getCode(), ResponseMessage.TICKET_NO_EXIST.getMessage());
        } catch (Exception e) {
            return new ApiResponse<>(ResponseMessage.EVENT_QR_FAILURE.getCode(), ResponseMessage.EVENT_QR_FAILURE.getMessage());
        }
    }
}
