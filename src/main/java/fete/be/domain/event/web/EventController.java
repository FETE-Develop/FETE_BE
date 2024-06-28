package fete.be.domain.event.web;

import com.google.zxing.NotFoundException;
import fete.be.domain.event.application.EventService;
import fete.be.domain.event.application.QRCodeService;
import fete.be.global.util.ApiResponse;
import fete.be.global.util.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    private final QRCodeService qrCodeService;


    /**
     * 이벤트 신청 후, QR 코드 발급 API
     *
     * @param Long posterId
     * @return ApiResponse<String>
     */
    @PostMapping("/{posterId}")
    public ApiResponse<String> applyEvent(@PathVariable("posterId") Long posterId) {
        try {
            // 해당 posterId로 이벤트 신청 후, QR 코드 발급하기
            String qrCode = eventService.applyEvent(posterId);
            return new ApiResponse<>(ResponseMessage.EVENT_QR_SUCCESS.getCode(), ResponseMessage.EVENT_QR_SUCCESS.getMessage(), qrCode);
        } catch (Exception e) {
            return new ApiResponse<>(ResponseMessage.EVENT_QR_FAILURE.getCode(), ResponseMessage.EVENT_QR_FAILURE.getMessage());
        }
    }


    /**
     * 이벤트 참여자의 QR 코드 검증 API
     * -> 유저의 QR 코드 검증, 이벤트 장소 검증
     *
     * @param MultipartFile file
     * @param Long posterId
     * @return ApiResponse
     */
    @PostMapping("/verify")
    public ApiResponse verifyQRCode(
            @RequestPart("file") MultipartFile file,
            @RequestPart("posterId") Long posterId) {
        try {
            // 유저의 QR 코드 검증, 이벤트 장소 검증
            Long participantId = qrCodeService.verifyQRCode(file, posterId);
            return new ApiResponse<>(ResponseMessage.EVENT_VALID_QR.getCode(), ResponseMessage.EVENT_VALID_QR.getMessage());
        } catch (IOException e) {
            return new ApiResponse<>(ResponseMessage.EVENT_INVALID_FILE.getCode(), ResponseMessage.EVENT_INVALID_FILE.getMessage());
        } catch (NotFoundException e) {
            return new ApiResponse<>(ResponseMessage.EVENT_INVALID_FILE.getCode(), ResponseMessage.EVENT_INVALID_FILE.getMessage());
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.EVENT_INVALID_QR.getCode(), ResponseMessage.EVENT_INVALID_QR.getMessage());
        }
    }
}
