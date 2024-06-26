package fete.be.domain.poster.web;

import fete.be.domain.member.application.MemberService;
import fete.be.domain.member.persistence.Member;
import fete.be.domain.poster.application.PosterService;
import fete.be.domain.poster.application.dto.request.ApprovePostersRequest;
import fete.be.domain.poster.application.dto.request.ModifyPosterRequest;
import fete.be.domain.poster.application.dto.request.WritePosterRequest;
import fete.be.domain.poster.application.dto.response.GetPostersResponse;
import fete.be.domain.poster.application.dto.response.PosterDto;
import fete.be.global.util.ApiResponse;
import fete.be.global.util.ResponseMessage;
import fete.be.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/posters")
public class PosterController {

    private final MemberService memberService;
    private final PosterService posterService;


    /**
     * 포스터 등록 API
     *
     * @param WritePosterRequest request
     * @return ApiResponse
     */
    @PostMapping
    public ApiResponse writePoster(@RequestBody WritePosterRequest request) {
        // 현재 요청한 Member의 email을 추출해서 member 찾아오기
        String email = SecurityUtil.getCurrentMemberEmail();
        Member findMember = memberService.findMemberByEmail(email);

        // request 정보, 찾은 member 정보를 서비스 단으로 넘겨서 포스터 생성 및 저장
        Long savedPosterId = posterService.writePoster(findMember, request);
        log.info("savedPosterId={}", savedPosterId);

        return new ApiResponse<>(ResponseMessage.SUCCESS.getCode(), ResponseMessage.SUCCESS.getMessage());
    }


    /**
     * 포스터 수정 API
     *
     * @param Long                posterId
     * @param ModifyPosterRequest request
     * @return ApiResponse
     */
    @PostMapping("/{posterId}")
    public ApiResponse modifyPoster(
            @PathVariable("posterId") Long posterId,
            @RequestBody ModifyPosterRequest request
    ) {
        try {
            // posterId로 포스터를 찾아 수정사항 업데이트
            Long updatePosterId = posterService.updatePoster(posterId, request);
            return new ApiResponse<>(ResponseMessage.POSTER_SUCCESS.getCode(), ResponseMessage.POSTER_SUCCESS.getMessage());

        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.POSTER_FAILURE.getCode(), e.getMessage());
        }
    }


    /**
     * 포스터 삭제 API
     *
     * @param Long posterId
     * @return ApiResponse
     */
    @DeleteMapping("/{posterId}")
    public ApiResponse deletePoster(@PathVariable("posterId") Long posterId) {
        try {
            // posterId로 포스터를 찾아 삭제 (소프트 삭제 방식)
            posterService.deletePoster(posterId);
            return new ApiResponse<>(ResponseMessage.POSTER_SUCCESS.getCode(), ResponseMessage.POSTER_SUCCESS.getMessage());

        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.POSTER_FAILURE.getCode(), e.getMessage());
        }
    }


    /**
     * 포스터 전체 조회 API
     *
     * @param int page
     * @param int size
     * @return ApiResponse<GetPostersResponse>
     */
    @GetMapping
    public ApiResponse<GetPostersResponse> getPosters(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        // 전체 포스터 페이징 처리해서 가져오기
        Pageable pageable = PageRequest.of(page, size);
        List<PosterDto> posters = posterService.getPosters(pageable).getContent();
        GetPostersResponse result = new GetPostersResponse(posters);

        return new ApiResponse<>(ResponseMessage.POSTER_SUCCESS.getCode(), ResponseMessage.POSTER_SUCCESS.getMessage(), result);
    }

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
            posterService.approvePosters(request);
            return new ApiResponse<>(ResponseMessage.POSTER_SUCCESS.getCode(), ResponseMessage.POSTER_SUCCESS.getMessage());
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.POSTER_FAILURE.getCode(), e.getMessage());
        }
    }
}
