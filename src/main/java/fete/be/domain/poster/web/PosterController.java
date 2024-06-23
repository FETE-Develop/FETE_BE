package fete.be.domain.poster.web;

import fete.be.domain.member.application.MemberService;
import fete.be.domain.member.persistence.Member;
import fete.be.domain.poster.application.PosterService;
import fete.be.domain.poster.application.dto.request.ModifyPosterRequest;
import fete.be.domain.poster.application.dto.request.WritePosterRequest;
import fete.be.domain.poster.persistence.Poster;
import fete.be.global.util.ApiResponse;
import fete.be.global.util.ResponseMessage;
import fete.be.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/posters")
public class PosterController {

    private final MemberService memberService;
    private final PosterService posterService;

    /**
     * 포스터 등록 API
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
     * @param Long posterId
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
     */

}
