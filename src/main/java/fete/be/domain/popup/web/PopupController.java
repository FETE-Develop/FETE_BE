package fete.be.domain.popup.web;

import fete.be.domain.popup.application.PopupService;
import fete.be.domain.popup.application.dto.GetPopupsResponse;
import fete.be.domain.popup.application.dto.PopupDto;
import fete.be.global.util.ApiResponse;
import fete.be.global.util.Logging;
import fete.be.global.util.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        try {
            log.info("GetPopups API");
            Logging.time();

            List<PopupDto> popups = popupService.getPopups();
            GetPopupsResponse result = new GetPopupsResponse(popups);

            return new ApiResponse<GetPopupsResponse>(ResponseMessage.POPUP_GET_POPUPS.getCode(), ResponseMessage.POPUP_GET_POPUPS.getMessage(), result);
        } catch (IllegalArgumentException e) {
            return new ApiResponse<GetPopupsResponse>(ResponseMessage.POPUP_GET_POPUPS_FAIL.getCode(), e.getMessage());
        }
    }
}
