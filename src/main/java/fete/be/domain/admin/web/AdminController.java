package fete.be.domain.admin.web;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessagingException;
import fete.be.domain.admin.application.dto.request.*;
import fete.be.domain.admin.application.dto.response.*;
import fete.be.domain.admin.exception.AdminOnlyAccessException;
import fete.be.domain.admin.exception.NotFoundNoticeException;
import fete.be.domain.banner.application.BannerService;
import fete.be.domain.banner.exception.AlreadyExistsPosterException;
import fete.be.domain.category.application.CategoryService;
import fete.be.domain.member.application.MemberService;
import fete.be.domain.member.application.dto.request.LoginRequestDto;
import fete.be.domain.member.application.dto.response.LoginResponseDto;
import fete.be.domain.notice.application.NoticeService;
import fete.be.domain.notification.application.NotificationService;
import fete.be.domain.notification.application.dto.request.PushMessageRequest;
import fete.be.domain.payment.application.PaymentService;
import fete.be.domain.popup.application.PopupService;
import fete.be.domain.popup.exception.NotFoundPopupException;
import fete.be.domain.poster.application.PosterService;
import fete.be.domain.poster.exception.NotFoundPosterException;
import fete.be.domain.poster.exception.ProfileImageCountMismatchException;
import fete.be.global.jwt.JwtToken;
import fete.be.global.util.ApiResponse;
import fete.be.global.util.Logging;
import fete.be.global.util.ResponseMessage;
import fete.be.global.util.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/admins")
public class AdminController {

    private final MemberService memberService;
    private final PosterService posterService;
    private final PaymentService paymentService;
    private final BannerService bannerService;
    private final PopupService popupService;
    private final CategoryService categoryService;
    private final NotificationService notificationService;
    private final NoticeService noticeService;


    /**
     * 관리자의 포스터 승인 API
     * : ADMIN 권한을 받은 후, 재 로그인 과정 필요
     *
     * @param ApprovePostersRequest request
     * @return ApiResponse
     */
    @PostMapping("/posters/approve")
    public ApiResponse approvePosters(@RequestBody ApprovePostersRequest request) {
        try {
            // 포스터 승인
            posterService.approvePosters(request);
            return new ApiResponse<>(ResponseMessage.ADMIN_APPROVE_POSTERS.getCode(), ResponseMessage.ADMIN_APPROVE_POSTERS.getMessage());
        } catch (NotFoundPosterException e) {
            return new ApiResponse<>(ResponseMessage.ADMIN_APPROVE_POSTERS_FAIL.getCode(), e.getMessage());
        }
    }


    /**
     * 관리자의 포스터 반려 API
     *
     * @param RejectPosterRequest request
     * @return ApiResponse
     */
    @PostMapping("/posters/reject")
    public ApiResponse rejectPoster(@RequestBody RejectPosterRequest request) {
        try {
            // 포스터 반려
            posterService.rejectPoster(request);
            return new ApiResponse<>(ResponseMessage.ADMIN_REJECT_POSTER.getCode(), ResponseMessage.ADMIN_REJECT_POSTER.getMessage());
        } catch (NotFoundPosterException e) {
            return new ApiResponse<>(ResponseMessage.ADMIN_REJECT_POSTER_FAIL.getCode(), e.getMessage());
        }
    }


    /**
     * 포스터 간편 조회 API
     *
     * @param String status
     * @param int    page
     * @param int    size
     * @return ApiResponse<GetSimplePostersResponse>
     */
    @GetMapping("/posters")
    public ApiResponse<GetSimplePostersResponse> getSimplePosters(
            @RequestParam(name = "status", defaultValue = "ACTIVE") String status,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        try {
            // status를 Status enum 타입으로 변환
            Status findStatus = Status.valueOf(status);
            List<SimplePosterDto> simplePosters = posterService.getSimplePosters(findStatus, page, size).getContent();
            GetSimplePostersResponse result = new GetSimplePostersResponse(simplePosters);

            return new ApiResponse<>(ResponseMessage.ADMIN_GET_POSTERS.getCode(), ResponseMessage.ADMIN_GET_POSTERS.getMessage(), result);
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.ADMIN_GET_POSTERS_FAIL.getCode(), e.getMessage());
        }
    }


    /**
     * 관리자의 아티스트 프로필 이미지 등록 API
     * - 단, 변경할 이미지 링크가 이미 등록된 아티스트 순서에 맞게 들어와야 함.
     *
     * @param Long                      posterId
     * @param SetArtistImageUrlsRequest request
     * @return ApiResponse
     */
    @PostMapping("/posters/{posterId}")
    public ApiResponse setArtistImageUrls(
            @PathVariable("posterId") Long posterId,
            @RequestBody SetArtistImageUrlsRequest request
    ) {
        try {
            // 아티스트 프로필 이미지 등록
            posterService.setArtistImageUrls(posterId, request);
            return new ApiResponse<>(ResponseMessage.ADMIN_REGISTER_ARTIST_PROFILE_SUCCESS.getCode(), ResponseMessage.ADMIN_REGISTER_ARTIST_PROFILE_SUCCESS.getMessage());
        } catch (ProfileImageCountMismatchException e) {
            return new ApiResponse<>(ResponseMessage.ADMIN_INVALID_ARTIST_PROFILE_COUNT.getCode(), e.getMessage());
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.ADMIN_REGISTER_ARTIST_PROFILE_FAIL.getCode(), e.getMessage());
        }
    }


    /**
     * 이벤트 간단 주소 변경 API
     *
     * @param Long posterId
     * @param ModifySimpleAddressRequest request
     * @return ApiResponse
     */
    @PostMapping("/posters/address/{posterId}")
    public ApiResponse modifySimpleAddress(
            @PathVariable("posterId") Long posterId,
            @RequestBody ModifySimpleAddressRequest request
    ) {
        // 간단 주소 추출
        String simpleAddress = request.getSimpleAddress();

        try {
            // 간단 주소 변경
            posterService.modifySimpleAddress(posterId, simpleAddress);

            return new ApiResponse<>(ResponseMessage.ADMIN_MODIFY_SIMPLE_ADDRESS_SUCCESS.getCode(), ResponseMessage.ADMIN_MODIFY_SIMPLE_ADDRESS_SUCCESS.getMessage());
        } catch (NotFoundPosterException e) {
            return new ApiResponse<>(ResponseMessage.ADMIN_MODIFY_SIMPLE_ADDRESS_FAIL.getCode(), e.getMessage());
        }
    }


    /**
     * 유저 정보 리스트 조회 API
     *
     * @param int page
     * @param int size
     * @return ApiResponse<GetMembersResponse>
     */
    @GetMapping
    public ApiResponse<GetMembersResponse> getMembers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        try {
            // 유저 정보 리스트 조회
            List<MemberDto> members = memberService.getMembers(page, size).getContent();
            GetMembersResponse result = new GetMembersResponse(members);

            return new ApiResponse<>(ResponseMessage.ADMIN_GET_MEMBERS.getCode(), ResponseMessage.ADMIN_GET_MEMBERS.getMessage(), result);
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.ADMIN_GET_MEMBERS_FAIL.getCode(), e.getMessage());
        }
    }


    /**
     * 유저 강제 탈퇴 API
     *
     * @param Long memberId
     * @return ApiResponse
     */
    @PostMapping("/members/deactivate/{memberId}")
    public ApiResponse deactivateMember(@PathVariable("memberId") Long memberId) {
        try {
            // 해당 유저 탈퇴 후, 휴대전화 번호를 차단 DB에 추가
            Long blockedMemberId = memberService.deactivateMember(memberId);

            return new ApiResponse<>(ResponseMessage.ADMIN_DEACTIVATE_MEMBER_SUCCESS.getCode(), ResponseMessage.ADMIN_DEACTIVATE_MEMBER_SUCCESS.getMessage());
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.ADMIN_DEACTIVATE_MEMBER_FAIL.getCode(), e.getMessage());
        }
    }


    /**
     * 이벤트의 결제 정보 조회 API
     *
     * @param Long posterId
     * @param int  page
     * @param int  size
     * @return ApiResponse<GetPaymentsResponse>
     */
    @GetMapping("/{posterId}")
    public ApiResponse<GetPaymentsResponse> getPayments(
            @PathVariable("posterId") Long posterId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        try {
            // 이벤트 결제 정보 조회
            List<PaymentDto> payments = paymentService.getPayments(posterId, page, size);
            int totalProfit = paymentService.getTotalProfit(posterId);
            AccountDto account = paymentService.getAccount(posterId);

            GetPaymentsResponse result = new GetPaymentsResponse(payments, totalProfit, account);

            return new ApiResponse<>(ResponseMessage.ADMIN_GET_PAYMENTS.getCode(), ResponseMessage.ADMIN_GET_PAYMENTS.getMessage(), result);
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.ADMIN_GET_PAYMENTS_FAIL.getCode(), e.getMessage());
        }
    }


    /**
     * 배너 생성 API
     *
     * @param CreateBannerRequest request
     * @return ApiResponse
     */
    @PostMapping("/banners")
    public ApiResponse createBanner(@RequestBody CreateBannerRequest request) {
        try {
            // 배너 생성
            Long savedBannerId = bannerService.createBanner(request);

            return new ApiResponse<>(ResponseMessage.ADMIN_CREATE_BANNER.getCode(), ResponseMessage.ADMIN_CREATE_BANNER.getMessage());
        } catch (AlreadyExistsPosterException e) {
            return new ApiResponse<>(ResponseMessage.ADMIN_CREATE_BANNER_FAIL.getCode(), e.getMessage());
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.ADMIN_CREATE_BANNER_FAIL.getCode(), e.getMessage());
        }
    }


    /**
     * 배너 수정 API
     *
     * @param Long                bannerId
     * @param ModifyBannerRequest request
     * @return ApiResponse
     */
    @PostMapping("/banners/{bannerId}")
    public ApiResponse modifyBanner(
            @PathVariable("bannerId") Long bannerId,
            @RequestBody ModifyBannerRequest request
    ) {
        try {
            // 배너 수정
            Long modifiedBanner = bannerService.modifyBanner(bannerId, request);

            return new ApiResponse<>(ResponseMessage.ADMIN_MODIFY_BANNER.getCode(), ResponseMessage.ADMIN_MODIFY_BANNER.getMessage());
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.ADMIN_MODIFY_BANNER_FAIL.getCode(), e.getMessage());
        }
    }


    /**
     * 배너 삭제 API
     *
     * @param Long bannerId
     * @return ApiResponse
     */
    @DeleteMapping("/banners/{bannerId}")
    public ApiResponse deleteBanner(@PathVariable("bannerId") Long bannerId) {
        try {
            // 배너 삭제
            bannerService.deleteBanner(bannerId);

            return new ApiResponse<>(ResponseMessage.ADMIN_DELETE_BANNER.getCode(), ResponseMessage.ADMIN_DELETE_BANNER.getMessage());
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.ADMIN_DELETE_BANNER_FAIL.getCode(), e.getMessage());
        }
    }


    /**
     * 팝업 생성 API
     *
     * @param CreatePopupRequest request
     * @return ApiResponse
     */
    @PostMapping("/popups")
    public ApiResponse createPopup(@RequestBody CreatePopupRequest request) {
        try {
            // 팝업 생성
            Long savedPopupId = popupService.createPopup(request);

            return new ApiResponse(ResponseMessage.ADMIN_CREATE_POPUP_SUCCESS.getCode(), ResponseMessage.ADMIN_CREATE_POPUP_SUCCESS.getMessage());
        } catch (IllegalArgumentException e) {
            return new ApiResponse(ResponseMessage.ADMIN_CREATE_POPUP_FAIL.getCode(), e.getMessage());
        }
    }


    /**
     * 팝업 수정 API
     *
     * @param Long               popupId
     * @param ModifyPopupRequest request
     * @return ApiResponse
     */
    @PostMapping("/popups/{popupId}")
    public ApiResponse modifyPopup(
            @PathVariable("popupId") Long popupId,
            @RequestBody ModifyPopupRequest request
    ) {
        try {
            // 팝업 수정
            Long modifiedPopupId = popupService.modifyPopup(popupId, request);

            return new ApiResponse(ResponseMessage.ADMIN_MODIFY_POPUP_SUCCESS.getCode(), ResponseMessage.ADMIN_MODIFY_POPUP_SUCCESS.getMessage());
        } catch (NotFoundPopupException e) {
            return new ApiResponse(ResponseMessage.ADMIN_MODIFY_POPUP_FAIL.getCode(), e.getMessage());
        }
    }


    /**
     * 팝업 삭제 API
     * - Hard 삭제 방식
     *
     * @param Long popupId
     * @return ApiResponse
     */
    @DeleteMapping("/popups/{popupId}")
    public ApiResponse deletePopup(@PathVariable("popupId") Long popupId) {
        try {
            // 팝업 삭제
            popupService.deletePopup(popupId);

            return new ApiResponse(ResponseMessage.ADMIN_DELETE_POPUP_SUCCESS.getCode(), ResponseMessage.ADMIN_DELETE_POPUP_SUCCESS.getMessage());
        } catch (NotFoundPopupException e) {
            return new ApiResponse(ResponseMessage.ADMIN_DELETE_POPUP_FAIL.getCode(), e.getMessage());
        }
    }


    /**
     * 카테고리 생성 API
     *
     * @param CreateCategoryRequest request
     * @return ApiResponse
     */
    @PostMapping("/categories")
    public ApiResponse createCategory(@RequestBody CreateCategoryRequest request) {
        try {
            // 카테고리 생성
            Long savedCategoryId = categoryService.createCategory(request);

            return new ApiResponse(ResponseMessage.ADMIN_CREATE_CATEGORY_SUCCESS.getCode(), ResponseMessage.ADMIN_CREATE_CATEGORY_SUCCESS.getMessage());
        } catch (IllegalArgumentException e) {
            return new ApiResponse(ResponseMessage.ADMIN_CREATE_CATEGORY_FAIL.getCode(), e.getMessage());
        }
    }


    /**
     * 카테고리 수정 API
     *
     * @param Long                  categoryId
     * @param ModifyCategoryRequest request
     * @return ApiResponse
     */
    @PostMapping("/categories/{categoryId}")
    public ApiResponse modifyCategory(
            @PathVariable("categoryId") Long categoryId,
            @RequestBody ModifyCategoryRequest request
    ) {
        try {
            // 카테고리 수정
            Long modifiedCategoryId = categoryService.modifyCategory(categoryId, request);

            return new ApiResponse(ResponseMessage.ADMIN_MODIFY_CATEGORY_SUCCESS.getCode(), ResponseMessage.ADMIN_MODIFY_CATEGORY_SUCCESS.getMessage());
        } catch (IllegalArgumentException e) {
            return new ApiResponse(ResponseMessage.ADMIN_MODIFY_CATEGORY_FAIL.getCode(), e.getMessage());
        }
    }


    /**
     * 카테고리 삭제 API
     *
     * @param Long categoryId
     * @return ApiResponse
     */
    @DeleteMapping("/categories/{categoryId}")
    public ApiResponse deleteCategory(@PathVariable("categoryId") Long categoryId) {
        try {
            // 카테고리 삭제
            categoryService.deleteCategory(categoryId);

            return new ApiResponse(ResponseMessage.ADMIN_DELETE_CATEGORY_SUCCESS.getCode(), ResponseMessage.ADMIN_DELETE_CATEGORY_SUCCESS.getMessage());
        } catch (IllegalArgumentException e) {
            return new ApiResponse(ResponseMessage.ADMIN_DELETE_CATEGORY_FAIL.getCode(), e.getMessage());
        }
    }


    /**
     * 알림 설정한 전체 유저에게 푸시 알림 전송 API
     */
    @PostMapping("/notifications")
    public ApiResponse sendAllMember(@RequestBody PushMessageRequest request) {
        try {
            // 알림 설정한 전체 유저에게 푸시 알림 전송
            BatchResponse response = notificationService.sendAll(request);

            return new ApiResponse(ResponseMessage.ADMIN_ALL_NOTIFICATION_SUCCESS.getCode(), ResponseMessage.ADMIN_ALL_NOTIFICATION_SUCCESS.getMessage());
        } catch (FirebaseMessagingException e) {
            return new ApiResponse(ResponseMessage.ADMIN_NOTIFICATION_FAILURE.getCode(), e.getMessage());
        } catch (IllegalArgumentException e) {
            return new ApiResponse(ResponseMessage.ADMIN_NOTIFICATION_FAILURE.getCode(), e.getMessage());
        }
    }


    /**
     * 공지사항 등록 API
     *
     * @param CreateNoticeRequest request
     * @return ApiResponse
     */
    @PostMapping("/notices")
    public ApiResponse createNotice(@RequestBody CreateNoticeRequest request) {
        try {
            // 공지사항 등록
            noticeService.createNotice(request);
            return new ApiResponse(ResponseMessage.ADMIN_CREATE_NOTICE_SUCCESS.getCode(), ResponseMessage.ADMIN_CREATE_NOTICE_SUCCESS.getMessage());
        } catch (Exception e) {
            return new ApiResponse(ResponseMessage.ADMIN_CREATE_NOTICE_FAIL.getCode(), e.getMessage());
        }
    }


    /**
     * 공지사항 수정 API
     *
     * @param Long                noticeId
     * @param ModifyNoticeRequest request
     * @return ApiResponse
     */
    @PostMapping("/notices/{noticeId}")
    public ApiResponse modifyNotice(
            @PathVariable("noticeId") Long noticeId,
            @RequestBody ModifyNoticeRequest request
    ) {
        try {
            // 공지사항 수정
            noticeService.modifyNotice(noticeId, request);
            return new ApiResponse(ResponseMessage.ADMIN_MODIFY_NOTICE_SUCCESS.getCode(), ResponseMessage.ADMIN_MODIFY_NOTICE_SUCCESS.getMessage());
        } catch (NotFoundNoticeException e) {
            return new ApiResponse(ResponseMessage.ADMIN_MODIFY_NOTICE_FAIL.getCode(), e.getMessage());
        }
    }


    /**
     * 공지사항 삭제 API
     *
     * @param Long noticeId
     * @return ApiResponse
     */
    @DeleteMapping("/notices/{noticeId}")
    public ApiResponse deleteNotice(@PathVariable("noticeId") Long noticeId) {
        try {
            // 공지사항 삭제
            noticeService.deleteNotice(noticeId);
            return new ApiResponse(ResponseMessage.ADMIN_DELETE_NOTICE_SUCCESS.getCode(), ResponseMessage.ADMIN_DELETE_NOTICE_SUCCESS.getMessage());
        } catch (NotFoundNoticeException e) {
            return new ApiResponse(ResponseMessage.ADMIN_DELETE_NOTICE_FAIL.getCode(), e.getMessage());
        }
    }


    /**
     * 관리자 로그인 API
     * - filterChain을 통해 ADMIN 권한을 가진 유저만 접근 가능하도록 설정
     *
     * @param LoginRequestDto request
     * @return ApiResponse<LoginResponseDto>
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponseDto> adminLogin(@RequestBody LoginRequestDto request) {
        try {
            // 로그인 검증 이후, 토큰 발급
            JwtToken token = memberService.adminLogin(request.getEmail(), request.getPassword());

            // 일치하는 유저가 없을 경우
            if (token == null) {
                return new ApiResponse(ResponseMessage.LOGIN_FAILURE.getCode(), ResponseMessage.LOGIN_FAILURE.getMessage());
            }

            // 일치하는 유저가 있는 경우 - 정상 로그인 로직
            LoginResponseDto result = new LoginResponseDto(token);
            return new ApiResponse<>(ResponseMessage.LOGIN_SUCCESS.getCode(), ResponseMessage.LOGIN_SUCCESS.getMessage(), result);
        } catch (AdminOnlyAccessException e) {
            return new ApiResponse<>(ResponseMessage.LOGIN_FAILURE.getCode(), ResponseMessage.IS_NOT_ADMIN.getMessage());
        }
    }

}
