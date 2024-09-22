package fete.be.domain.popup.web;

import fete.be.domain.member.exception.GuestUserException;
import fete.be.domain.popup.application.PopupService;
import fete.be.domain.popup.application.dto.GetPopupsResponse;
import fete.be.domain.popup.application.dto.PopupDto;
import fete.be.domain.popup.exception.NotFoundPopupException;
import fete.be.global.util.ApiResponse;
import fete.be.global.util.Logging;
import fete.be.global.util.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/popups")
public class PopupController {

    private final PopupService popupService;


    /**
     * 팝업 전체 조회 API
     *
     * @return ApiResponse<GetPopupsResponse>
     */
    @GetMapping
    public ApiResponse<GetPopupsResponse> getPopups() {
        log.info("GetPopups API");
        Logging.time();

        try {
            List<PopupDto> popups = popupService.getPopups();
            GetPopupsResponse result = new GetPopupsResponse(popups);

            return new ApiResponse<>(ResponseMessage.POPUP_GET_POPUPS.getCode(), ResponseMessage.POPUP_GET_POPUPS.getMessage(), result);
        } catch (GuestUserException e) {
            // 게스트용 팝업 조회
            List<PopupDto> popups = popupService.getGuestPopups();
            GetPopupsResponse result = new GetPopupsResponse(popups);

            return new ApiResponse<>(ResponseMessage.POPUP_GET_POPUPS.getCode(), ResponseMessage.POPUP_GET_POPUPS.getMessage(), result);
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.POPUP_GET_POPUPS_FAIL.getCode(), e.getMessage());
        }
    }


    /**
     * 팝업 그만보기 API
     *
     * @param Long popupId
     * @return ApiResponse
     */
    @PostMapping("/dismiss/{popupId}")
    public ApiResponse dismissPopup(@PathVariable("popupId") Long popupId) {
        log.info("DismissPopup API");
        Logging.time();

        try {
            // 팝업 그만보기 실행
            popupService.dismissPopup(popupId);

            return new ApiResponse<GetPopupsResponse>(ResponseMessage.POPUP_GET_POPUPS.getCode(), ResponseMessage.POPUP_GET_POPUPS.getMessage());
        } catch (NotFoundPopupException e) {
            return new ApiResponse<GetPopupsResponse>(ResponseMessage.POPUP_GET_POPUPS_FAIL.getCode(), e.getMessage());
        }
    }
}
