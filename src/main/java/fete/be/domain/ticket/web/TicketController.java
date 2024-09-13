package fete.be.domain.ticket.web;

import fete.be.domain.event.exception.NotFoundEventException;
import fete.be.domain.member.exception.GuestUserException;
import fete.be.domain.payment.application.TossService;
import fete.be.domain.payment.exception.InvalidCancelReasonException;
import fete.be.domain.payment.exception.InvalidPaymentStatusException;
import fete.be.domain.ticket.application.dto.request.CancelTicketsRequest;
import fete.be.domain.ticket.application.dto.request.GetEventTicketsRequest;
import fete.be.domain.ticket.application.dto.response.*;
import fete.be.domain.ticket.application.TicketService;
import fete.be.domain.ticket.exception.InvalidRefundAmountException;
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
     * 구매한 티켓의 이벤트 목록 조회 API
     *
     * @return ApiResponse<GetMyTicketsEventResponse>
     */
    @GetMapping
    public ApiResponse<GetMyTicketsEventResponse> getEvents() {
        log.info("GetTickets request");
        Logging.time();

        try {
            List<TicketEventDto> ticketEventInfos = ticketService.getEvents();
            GetMyTicketsEventResponse result = new GetMyTicketsEventResponse(ticketEventInfos);

            return new ApiResponse<>(ResponseMessage.TICKET_SUCCESS.getCode(), ResponseMessage.TICKET_SUCCESS.getMessage(), result);
        } catch (GuestUserException e) {
            return new ApiResponse<>(ResponseMessage.MEMBER_NO_EXIST.getCode(), e.getMessage());
        }
    }


    /**
     * 이벤트에서 구매한 나의 티켓 주문 내역 조회 API
     * - eventId와 paymentCode를 전달 받기
     * @return 이벤트 정보, 해당 이벤트에서 주문한 티켓 정보들(결제 상태, 가격, 종류, participantId)
     * -> eventId로 이벤트 정보 조회(이벤트 이름, 시작 시간, 종료 시간, 주소, 대표 이미지)
     * -> paymentCode로 Payment(=티켓) 조회(결제 상태, 가격, 종류, participantId)해서 리스트화
     */
    @GetMapping("/event-tickets")
    public ApiResponse<GetEventTicketsResponse> getEventTickets(@RequestBody GetEventTicketsRequest request) {
        log.info("GetEventTickets request={}", request);
        Logging.time();

        // 요청 값 추출
        Long eventId = request.getEventId();
        String paymentCode = request.getPaymentCode();

        try {
            // 이벤트 정보
            SimpleEventDto eventDto = ticketService.getEventInfo(eventId);

            // 해당 이벤트에서 주문한 티켓 정보들
            List<SimpleTicketDto> ticketDtos = ticketService.getTickets(paymentCode);

            GetEventTicketsResponse result = new GetEventTicketsResponse(eventDto, ticketDtos);
            return new ApiResponse<>(ResponseMessage.TICKET_SUCCESS.getCode(), ResponseMessage.TICKET_SUCCESS.getMessage(), result);
        } catch (NotFoundEventException e) {
            return new ApiResponse<>(ResponseMessage.EVENT_NO_EXIST.getCode(), e.getMessage());
        }
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
        } catch (InvalidPaymentStatusException e) {
            return new ApiResponse<>(ResponseMessage.TICKET_IS_NOT_PAID.getCode(), e.getMessage());
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
    @PostMapping
    public ApiResponse cancelTickets(@RequestBody CancelTicketsRequest request) {
        log.info("CancelTicket request: request={}", request);
        Logging.time();

        // 취소할 티켓 리스트
        List<Long> ticketIds = request.getTicketIds();
        String cancelReason = request.getCancelReason();

        try {
            for (Long participantId : ticketIds) {
                // 티켓 개별 취소 실행
                tossService.cancelTicket(participantId, cancelReason);
            }
            return new ApiResponse<>(ResponseMessage.TICKET_CANCEL_SUCCESS.getCode(), ResponseMessage.TICKET_CANCEL_SUCCESS.getMessage());
        } catch (InvalidCancelReasonException e) {
            return new ApiResponse<>(ResponseMessage.TICKET_CANCEL_FAILURE.getCode(), e.getMessage());
        } catch (InvalidRefundAmountException e) {
            return new ApiResponse<>(ResponseMessage.TICKET_CANCEL_FAILURE.getCode(), e.getMessage());
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.TICKET_CANCEL_FAILURE.getCode(), e.getMessage());
        }
    }


    /**
     * 유저의 CustomerKey 조회 API
     *
     * @return ApiResponse<String>
     */
    @GetMapping("/customer-key")
    public ApiResponse<String> getCustomerKey() {
        log.info("getCustomerKey request");
        Logging.time();

        try {
            String customerKey = ticketService.getCustomerKey();
            return new ApiResponse<>(ResponseMessage.TICKET_GET_CUSTOMER_KEY_SUCCESS.getCode(), ResponseMessage.TICKET_GET_CUSTOMER_KEY_SUCCESS.getMessage(), customerKey);
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.TICKET_GET_CUSTOMER_KEY_FAILURE.getCode(), e.getMessage());

        }
    }
}
