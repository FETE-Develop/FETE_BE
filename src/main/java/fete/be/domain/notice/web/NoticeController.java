package fete.be.domain.notice.web;

import fete.be.domain.admin.exception.NotFoundNoticeException;
import fete.be.domain.notice.application.NoticeService;
import fete.be.domain.notice.application.dto.GetNoticeResponse;
import fete.be.domain.notice.application.dto.GetNoticesResponse;
import fete.be.domain.notice.application.dto.SimpleNotice;
import fete.be.global.util.ApiResponse;
import fete.be.global.util.Logging;
import fete.be.global.util.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notices")
@Slf4j
public class NoticeController {
    private final NoticeService noticeService;


    /**
     * 공지사항 전체 목록 조회 API
     *
     * @param int page
     * @param int size
     * @return ApiResponse<GetNoticesResponse>
     */
    @GetMapping
    public ApiResponse<GetNoticesResponse> getNotices(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        try {
            // 공지사항 전체 목록 조회
            List<SimpleNotice> simpleNotices = noticeService.getNotices(page, size).getContent();
            GetNoticesResponse result = new GetNoticesResponse(simpleNotices);

            return new ApiResponse<>(ResponseMessage.NOTICE_GET_SIMPLE_NOTICES.getCode(), ResponseMessage.NOTICE_GET_SIMPLE_NOTICES.getMessage(), result);
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.NOTICE_GET_SIMPLE_NOTICES_FAIL.getCode(), e.getMessage());
        }
    }


    /**
     * 공지사항 단건 조회 API
     *
     * @param Long noticeId
     * @return ApiResponse<GetNoticeResponse>
     */
    @GetMapping("/{noticeId}")
    public ApiResponse<GetNoticeResponse> getNotice(@PathVariable("noticeId") Long noticeId) {
        try {
            // 공지사항 단건 조회
            GetNoticeResponse result = noticeService.getNotice(noticeId);

            return new ApiResponse<>(ResponseMessage.NOTICE_GET_NOTICE.getCode(), ResponseMessage.NOTICE_GET_NOTICE.getMessage(), result);
        } catch (NotFoundNoticeException e) {
            return new ApiResponse<>(ResponseMessage.NOTICE_GET_NOTICE_FAIL.getCode(), e.getMessage());
        }
    }
}
