package fete.be.domain.event.web;

import fete.be.domain.admin.application.dto.response.AccountDto;
import fete.be.domain.admin.application.dto.response.GetPaymentsResponse;
import fete.be.domain.admin.application.dto.response.PaymentDto;
import fete.be.domain.event.application.EventService;
import fete.be.domain.event.application.QRCodeService;
import fete.be.domain.event.application.dto.request.BuyTicketRequest;
import fete.be.domain.event.application.dto.request.CheckTicketsQuantityRequest;
import fete.be.domain.event.application.dto.request.GrantTempManagerRequest;
import fete.be.domain.event.application.dto.request.ParticipantDto;
import fete.be.domain.event.application.dto.response.BuyTicketResponse;
import fete.be.domain.event.application.dto.response.GetManagerCodeResponse;
import fete.be.domain.event.exception.*;
import fete.be.domain.payment.application.PaymentService;
import fete.be.domain.payment.exception.InvalidTossResponseException;
import fete.be.domain.poster.application.PosterService;
import fete.be.domain.poster.exception.NotFoundPosterException;
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
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    private final QRCodeService qrCodeService;
    private final PaymentService paymentService;
    private final PosterService posterService;


    /**
     * 티켓 구매 API
     *
     * @param Long posterId
     * @return ApiResponse<String>
     */
    @PostMapping("/{posterId}")
    public ApiResponse<BuyTicketResponse> buyTicket(
            @PathVariable("posterId") Long posterId,
            @RequestBody BuyTicketRequest buyTicketRequest) {
        try {
            log.info("BuyTicket request: {}", posterId);
            Logging.time();

            // 해당 posterId로 이벤트 신청 후, QR 코드 발급하기
            List<String> qrCodes = eventService.buyTicket(posterId, buyTicketRequest);
            BuyTicketResponse result = new BuyTicketResponse(qrCodes);

            return new ApiResponse<>(ResponseMessage.EVENT_QR_SUCCESS.getCode(), ResponseMessage.EVENT_QR_SUCCESS.getMessage(), result);
        } catch (IncorrectPaymentAmountException e) {
            return new ApiResponse<>(ResponseMessage.EVENT_QR_FAILURE.getCode(), e.getMessage());
        } catch (IncorrectTicketPriceException e) {
            return new ApiResponse<>(ResponseMessage.EVENT_QR_FAILURE.getCode(), e.getMessage());
        } catch (IncorrectTicketTypeException e) {
            return new ApiResponse<>(ResponseMessage.EVENT_QR_FAILURE.getCode(), e.getMessage());
        } catch (InsufficientTicketsException e) {
            return new ApiResponse<>(ResponseMessage.EVENT_QR_FAILURE.getCode(), e.getMessage());
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.EVENT_QR_FAILURE.getCode(), e.getMessage());
        } catch (AlreadyPaymentStateException e) {
            return new ApiResponse<>(ResponseMessage.EVENT_QR_FAILURE.getCode(), e.getMessage());
        } catch (InvalidTossResponseException e) {
            return new ApiResponse<>(ResponseMessage.INVALID_TOSS_PAYMENT_API_RESPONSE.getCode(), e.getMessage());
        } catch (Exception e) {
            return new ApiResponse<>(ResponseMessage.EVENT_QR_FAILURE.getCode(), e.getMessage());
        }
    }


    /**
     * 티켓 QR 코드 인증 API
     *
     * @param Long           posterId
     * @param ParticipantDto request
     * @return
     */
    @PostMapping("/verify/{posterId}")
    public ApiResponse verifyQRCode(
            @PathVariable("posterId") Long posterId,
            @RequestBody ParticipantDto request
    ) {
        log.info("VerifyQRCode request: posterId={}", posterId);
        Logging.time();

        try {
            // 유저의 QR 코드 검증, 이벤트 장소 검증
            Long participantId = qrCodeService.verifyQRCode(posterId, request);

            return new ApiResponse<>(ResponseMessage.EVENT_VALID_QR.getCode(), ResponseMessage.EVENT_VALID_QR.getMessage());
        } catch (NotFoundPosterException e) {
            log.info("Error: {}", e.getMessage());
            return new ApiResponse<>(ResponseMessage.POSTER_NO_EXIST.getCode(), e.getMessage());
        } catch (AlreadyUsedQRCodeException e) {
            log.info("Error: {}", e.getMessage());
            return new ApiResponse<>(ResponseMessage.EVENT_QR_ALREADY_USED.getCode(), e.getMessage());
        } catch (InvalidEventPlaceException e) {
            log.info("Error: {}", e.getMessage());
            return new ApiResponse<>(ResponseMessage.EVENT_INVALID_PLACE.getCode(), e.getMessage());
        } catch (IncorrectQRCodeException e) {
            log.info("Error: {}", e.getMessage());
            return new ApiResponse<>(ResponseMessage.EVENT_INVALID_QR.getCode(), e.getMessage());
        } catch (AccessDeniedException e) {
            log.info("Error: {}", e.getMessage());
            return new ApiResponse<>(ResponseMessage.EVENT_INCORRECT_MANAGER.getCode(), e.getMessage());
        }
    }


    /**
     * 이벤트 담당자의 결제 정보 조회 API
     *
     * @param Long posterId
     * @param int  page
     * @param int  size
     * @return ApiResponse<GetPaymentsResponse>
     */
    @GetMapping("/payments-info/{posterId}")
    public ApiResponse<GetPaymentsResponse> getPayments(
            @PathVariable("posterId") Long posterId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        log.info("GetPayments API: posterId={}", posterId);
        Logging.time();

        try {
            // 이벤트 담당자 검증
            eventService.checkEventManager(posterId);

            List<PaymentDto> payments = paymentService.getPayments(posterId, page, size);
            int totalProfit = paymentService.getTotalProfit(posterId);
            AccountDto account = paymentService.getAccount(posterId);

            GetPaymentsResponse result = new GetPaymentsResponse(payments, totalProfit, account);

            return new ApiResponse<>(ResponseMessage.ADMIN_GET_PAYMENTS.getCode(), ResponseMessage.ADMIN_GET_PAYMENTS.getMessage(), result);
        } catch (IncorrectEventManagerException e) {
            return new ApiResponse<>(ResponseMessage.ADMIN_GET_PAYMENTS_FAIL.getCode(), e.getMessage());
        } catch (NotFoundPosterException e) {
            return new ApiResponse<>(ResponseMessage.ADMIN_GET_PAYMENTS_FAIL.getCode(), e.getMessage());
        }
    }


    /**
     * 구매하려는 티켓의 수량이 충분한지 확인하는 API
     *
     * @param Long posterId
     * @param CheckTicketsQuantityRequest checkTicketsQuantityRequest
     * @return
     */
    @PostMapping("/{posterId}/tickets/check")
    public ApiResponse checkTicketsQuantity(
            @PathVariable("posterId") Long posterId,
            @RequestBody CheckTicketsQuantityRequest checkTicketsQuantityRequest
    ) {
        log.info("CheckTicketsQuantity request: {}", checkTicketsQuantityRequest);
        Logging.time();

        try {
            // 티켓 수량 검사
            eventService.checkTicketsQuantity(posterId, checkTicketsQuantityRequest);

            return new ApiResponse<>(ResponseMessage.TICKET_ENOUGH_QUANTITY.getCode(), ResponseMessage.TICKET_ENOUGH_QUANTITY.getMessage());
        } catch (InsufficientTicketsException e) {
            return new ApiResponse<>(ResponseMessage.TICKET_NOT_ENOUGH_QUANTITY.getCode(), e.getMessage());
        } catch (IncorrectTicketPriceException e) {
            return new ApiResponse<>(ResponseMessage.TICKET_NOT_ENOUGH_QUANTITY.getCode(), e.getMessage());
        } catch (IncorrectTicketTypeException e) {
            return new ApiResponse<>(ResponseMessage.TICKET_NOT_ENOUGH_QUANTITY.getCode(), e.getMessage());
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.TICKET_NOT_ENOUGH_QUANTITY.getCode(), e.getMessage());
        }
    }


    /**
     * 포스터 고유식별코드 조회 API
     *
     * @param Long posterId
     * @return ApiResponse<GetManagerCodeResponse>
     */
    @GetMapping("/{posterId}/manager-code")
    public ApiResponse<GetManagerCodeResponse> getManagerCode(@PathVariable("posterId") Long posterId) {
        log.info("GetManagerCode request: {}", posterId);
        Logging.time();

        try {
            // 해당 포스터 고유식별코드 조회
            String managerCode = posterService.getManagerCode(posterId);

            GetManagerCodeResponse result = new GetManagerCodeResponse(managerCode);
            return new ApiResponse<>(ResponseMessage.POSTER_CODE_SUCCESS.getCode(), ResponseMessage.POSTER_CODE_SUCCESS.getMessage(), result);
        } catch (NotFoundPosterException e) {
            return new ApiResponse<>(ResponseMessage.POSTER_CODE_FAILURE.getCode(), e.getMessage());
        } catch (AccessDeniedException e) {
            return new ApiResponse<>(ResponseMessage.POSTER_CODE_FAILURE.getCode(), e.getMessage());
        }
    }


    /**
     * 임시 담당자 권한 부여 API
     * - 포스터 고유식별코드를 입력하여 해당 유저에게 임시 담당자 권한 부여
     *
     * @param GrantTempManagerRequest grantTempManagerRequest
     * @return
     */
    @PostMapping("/{posterId}/temp-manager")
    public ApiResponse grantTempManager(@RequestBody GrantTempManagerRequest grantTempManagerRequest) {
        log.info("GrantTempManager API");
        Logging.time();

        try {
            // 코드를 입력하여 임시 담당자 권한 부여
            String managerCode = grantTempManagerRequest.getManagerCode();
            eventService.grantTempManager(managerCode);

            return new ApiResponse<>(ResponseMessage.TEMP_MANAGER_SUCCESS.getCode(), ResponseMessage.TEMP_MANAGER_SUCCESS.getMessage());
        } catch (NotFoundPosterException e) {
            return new ApiResponse<>(ResponseMessage.TEMP_MANAGER_FAILURE.getCode(), e.getMessage());
        }
    }
}
