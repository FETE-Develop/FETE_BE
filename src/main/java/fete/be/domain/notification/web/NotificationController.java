package fete.be.domain.notification.web;

import fete.be.domain.notification.application.NotificationService;
import fete.be.domain.notification.application.dto.request.StoreTokenRequest;
import fete.be.global.util.ApiResponse;
import fete.be.global.util.Logging;
import fete.be.global.util.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;


    /**
     * 유저의 FCM 토큰 저장 API
     *
     * @param StoreTokenRequest request
     * @return ApiResponse
     */
    @PostMapping
    public ApiResponse storeToken(@RequestBody StoreTokenRequest request) {
        // FCM 토큰 추출
        String fcmToken = request.getToken();

        try {
            // FCM 토큰 저장
            Long savedMemberId = notificationService.storeToken(fcmToken);

            return new ApiResponse(ResponseMessage.NOTIFICATION_STORE_FCM_TOKEN.getCode(), ResponseMessage.NOTIFICATION_STORE_FCM_TOKEN.getMessage());
        } catch (IllegalArgumentException e) {
            return new ApiResponse(ResponseMessage.NOTIFICATION_STORE_FCM_TOKEN_FAILURE.getCode(), e.getMessage());
        }
    }

}
