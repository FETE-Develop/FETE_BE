package fete.be.domain.event.web;

import com.google.zxing.NotFoundException;
import fete.be.domain.event.application.EventService;
import fete.be.domain.event.application.QRCodeService;
import fete.be.domain.event.application.dto.request.BuyTicketRequest;
import fete.be.domain.event.application.dto.request.CheckTicketsQuantityRequest;
import fete.be.domain.event.application.dto.request.VerifyQRCodeRequest;
import fete.be.domain.event.application.dto.response.BuyTicketResponse;
import fete.be.domain.event.exception.IncorrectPaymentAmountException;
import fete.be.domain.event.exception.IncorrectTicketPriceException;
import fete.be.domain.event.exception.IncorrectTicketTypeException;
import fete.be.domain.event.exception.InsufficientTicketsException;
import fete.be.global.util.ApiResponse;
import fete.be.global.util.Logging;
import fete.be.global.util.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    private final QRCodeService qrCodeService;


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
        } catch (Exception e) {
            return new ApiResponse<>(ResponseMessage.EVENT_QR_FAILURE.getCode(), e.getMessage());
        }
    }


    @PostMapping("/verify/{posterId}")
    public ApiResponse verifyQRCode(
            @PathVariable("posterId") Long posterId,
            @RequestBody VerifyQRCodeRequest request
    ) {
        log.info("VerifyQRCode request: posterId={}", posterId);
        Logging.time();

        // QR 정보 (Base64Image)
        String value = request.getValue();

        try {
            // 유저의 QR 코드 검증, 이벤트 장소 검증
            Long participantId = qrCodeService.verifyQRCode(posterId, value);

            return new ApiResponse<>(ResponseMessage.EVENT_VALID_QR.getCode(), ResponseMessage.EVENT_VALID_QR.getMessage());
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.EVENT_INVALID_QR.getCode(), e.getMessage());
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//
//    @PostMapping("/verify")
//    public ApiResponse verifyQRCode(
//            @RequestPart("posterId") Long posterId,
//            @RequestPart MultipartFile file
//    ) {
//        try {
//
//            // 유저의 QR 코드 검증, 이벤트 장소 검증
//            Long participantId = qrCodeService.verifyQRCode(file, posterId);
//
//            return new ApiResponse<>(ResponseMessage.EVENT_VALID_QR.getCode(), ResponseMessage.EVENT_VALID_QR.getMessage());
//        } catch (IllegalArgumentException e) {
//            return new ApiResponse<>(ResponseMessage.EVENT_INVALID_QR.getCode(), e.getMessage());
//        } catch (NotFoundException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    /**
     * 구매하려는 티켓의 수량이 충분한지 확인하는 API
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
     * 이벤트 참여자의 QR 코드 검증 API
     * -> 유저의 QR 코드 검증, 이벤트 장소 검증
     *
     * @param MultipartFile file
     * @param Long          posterId
     * @return ApiResponse
     */
//    @PostMapping("/verify")
//    public ApiResponse verifyQRCode(
//            @RequestPart("file") MultipartFile file,
//            @RequestPart("posterId") Long posterId) {
//        try {
//            log.info("VerifyQRCode request: {}", posterId);
//            Logging.time();
//
//            // 유저의 QR 코드 검증, 이벤트 장소 검증
//            Long participantId = qrCodeService.verifyQRCode(file, posterId);
//            return new ApiResponse<>(ResponseMessage.EVENT_VALID_QR.getCode(), ResponseMessage.EVENT_VALID_QR.getMessage());
//        } catch (IOException e) {
//            return new ApiResponse<>(ResponseMessage.EVENT_INVALID_FILE.getCode(), ResponseMessage.EVENT_INVALID_FILE.getMessage());
//        } catch (NotFoundException e) {
//            return new ApiResponse<>(ResponseMessage.EVENT_INVALID_FILE.getCode(), ResponseMessage.EVENT_INVALID_FILE.getMessage());
//        } catch (IllegalArgumentException e) {
//            return new ApiResponse<>(ResponseMessage.EVENT_INVALID_QR.getCode(), e.getMessage());
//        }
//    }
}
