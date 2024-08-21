package fete.be.domain.ticket.web;

import fete.be.domain.payment.application.TossService;
import fete.be.domain.payment.application.dto.request.TossCancelRequest;
import fete.be.domain.ticket.application.dto.response.GetTicketInfoResponse;
import fete.be.domain.ticket.application.dto.response.GetTicketsResponse;
import fete.be.domain.ticket.application.dto.response.TicketDto;
import fete.be.domain.ticket.application.TicketService;
import fete.be.global.util.ApiResponse;
import fete.be.global.util.Logging;
import fete.be.global.util.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final TossService tossService;


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
     * - QR 코드, 이벤트 정보 조회
     *
     * @param Long participantId
     * @return ApiResponse<GetTicketInfoResponse>
     */
    @GetMapping("/{participantId}")
    public ApiResponse<GetTicketInfoResponse> getTicketInfo(@PathVariable("participantId") Long participantId) {
        log.info("GetTicketInfo request");
        Logging.time();

        try {
            // 티켓 1개 상세 조회
            GetTicketInfoResponse result = ticketService.getTicketInfo(participantId);

            return new ApiResponse<>(ResponseMessage.TICKET_SUCCESS.getCode(), ResponseMessage.TICKET_SUCCESS.getMessage(), result);
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.TICKET_NO_EXIST.getCode(), e.getMessage());
        } catch (Exception e) {
            return new ApiResponse<>(ResponseMessage.EVENT_QR_FAILURE.getCode(), ResponseMessage.EVENT_QR_FAILURE.getMessage());
        }
    }


    /**
     * 티켓 취소 API
     *
     * @param Long              participantId
     * @param TossCancelRequest request
     * @return ApiResponse
     */
    @PostMapping("/{participantId}")
    public ApiResponse cancelTicket(
            @PathVariable("participantId") Long participantId,
            @RequestBody TossCancelRequest request
    ) {
        log.info("CancelTicket request: participantId={}, request={}", participantId, request);
        Logging.time();

        try {
            // 토스 결제 취소 API 실행
            String cancelReason = request.getCancelReason();
            String transactionKey = tossService.cancelPayment(participantId, cancelReason);

            return new ApiResponse<>(ResponseMessage.TICKET_CANCEL_SUCCESS.getCode(), ResponseMessage.TICKET_CANCEL_SUCCESS.getMessage());
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.TICKET_CANCEL_FAILURE.getCode(), e.getMessage());
        }
    }
}
