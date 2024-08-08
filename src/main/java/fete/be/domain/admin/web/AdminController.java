package fete.be.domain.admin.web;

import fete.be.domain.admin.application.dto.response.GetMembersResponse;
import fete.be.domain.admin.application.dto.response.MemberDto;
import fete.be.domain.member.application.MemberService;
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
}
