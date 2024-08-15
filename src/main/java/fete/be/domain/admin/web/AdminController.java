package fete.be.domain.admin.web;

import fete.be.domain.admin.application.dto.response.*;
import fete.be.domain.banner.application.BannerService;
import fete.be.domain.admin.application.dto.request.CreateBannerRequest;
import fete.be.domain.member.application.MemberService;
import fete.be.domain.payment.application.PaymentService;
import fete.be.domain.poster.application.PosterService;
import fete.be.domain.admin.application.dto.request.ApprovePostersRequest;
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
@RequestMapping("/api/admins")
public class AdminController {

    private final MemberService memberService;
    private final PosterService posterService;
    private final PaymentService paymentService;
    private final BannerService bannerService;


    /**
     * 관리자의 포스터 승인 API
     * : ADMIN 권한을 받은 후, 재 로그인 과정 필요
     *
     * @param ApprovePostersRequest request
     * @return ApiResponse
     */
    @PostMapping("/approve")
    public ApiResponse approvePosters(@RequestBody ApprovePostersRequest request) {
        try {
            log.info("ApprovePosters request={}", request);
            Logging.time();

            posterService.approvePosters(request);
            return new ApiResponse<>(ResponseMessage.ADMIN_APPROVE_POSTERS.getCode(), ResponseMessage.ADMIN_APPROVE_POSTERS.getMessage());
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.ADMIN_APPROVE_POSTERS_FAIL.getCode(), e.getMessage());
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
            log.info("GetMembers API");
            Logging.time();

            List<MemberDto> members = memberService.getMembers(page, size).getContent();
            GetMembersResponse result = new GetMembersResponse(members);

            return new ApiResponse<>(ResponseMessage.ADMIN_GET_MEMBERS.getCode(), ResponseMessage.ADMIN_GET_MEMBERS.getMessage(), result);
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.ADMIN_GET_MEMBERS_FAIL.getCode(), e.getMessage());
        }
    }


    /**
     * 이벤트의 결제 정보 조회 API
     *
     * @param Long posterId
     * @param int page
     * @param int size
     * @return ApiResponse<GetPaymentsResponse>
     */
    @GetMapping("/{posterId}")
    public ApiResponse<GetPaymentsResponse> getPayments(
            @PathVariable("posterId") Long posterId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        try {
            log.info("GetPayments API: posterId={}", posterId);
            Logging.time();

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
            log.info("CreateBanner API: request={}", request);
            Logging.time();

            // 배너 생성
            Long savedBannerId = bannerService.createBanner(request);

            return new ApiResponse<>(ResponseMessage.ADMIN_CREATE_BANNER.getCode(), ResponseMessage.ADMIN_CREATE_BANNER.getMessage());
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.ADMIN_CREATE_BANNER_FAIL.getCode(), e.getMessage());
        }
    }
}