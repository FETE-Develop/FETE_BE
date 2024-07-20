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
import fete.be.global.util.Logging;
import fete.be.global.util.ResponseMessage;
import fete.be.global.util.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posters")
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
        log.info("WritePoster request={}", request);
        Logging.time();

        // 현재 요청한 Member의 email을 추출해서 member 찾아오기
        Member findMember = memberService.findMemberByEmail();

        // request 정보, 찾은 member 정보를 서비스 단으로 넘겨서 포스터 생성 및 저장
        Long savedPosterId = posterService.writePoster(findMember, request);

        return new ApiResponse<>(ResponseMessage.POSTER_SUCCESS.getCode(), ResponseMessage.POSTER_SUCCESS.getMessage());
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
            log.info("ModifyPoster request: posterId={}, request={}", posterId, request);
            Logging.time();

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
            log.info("DeletePoster request: posterId={}", posterId);
            Logging.time();

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
            @RequestParam(name = "status", defaultValue = "ACTIVE") String status,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        log.info("GetPosters request: status={}", status);
        Logging.time();

        // status를 Status enum 타입으로 변환
        Status findStatus = Status.valueOf(status);

        List<PosterDto> posters = posterService.getPosters(findStatus, page, size).getContent();
        GetPostersResponse result = new GetPostersResponse(posters);

        return new ApiResponse<>(ResponseMessage.POSTER_SUCCESS.getCode(), ResponseMessage.POSTER_SUCCESS.getMessage(), result);
    }


    /**
     * 포스터 단건 조회 API
     *
     * @param Long posterId
     * @return ApiResponse<PosterDto>
     */
    @GetMapping("/{posterId}")
    public ApiResponse<PosterDto> getPoster(
            @PathVariable("posterId") Long posterId,
            @RequestParam(name = "status", defaultValue = "ACTIVE") String status) {
        try {
            log.info("GetPoster request: posterId={}, status={}", posterId, status);
            Logging.time();

            // status를 Status enum 타입으로 변환
            Status findStatus = Status.valueOf(status);

            PosterDto result = posterService.getPoster(posterId, findStatus);
            return new ApiResponse<>(ResponseMessage.POSTER_SUCCESS.getCode(), ResponseMessage.POSTER_SUCCESS.getMessage(), result);
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.POSTER_INVALID_POSTER.getCode(), e.getMessage());
        }
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
            log.info("ApprovePosters request={}", request);
            Logging.time();

            posterService.approvePosters(request);
            return new ApiResponse<>(ResponseMessage.POSTER_SUCCESS.getCode(), ResponseMessage.POSTER_SUCCESS.getMessage());
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.POSTER_FAILURE.getCode(), e.getMessage());
        }
    }


    /**
     * 자신이 등록한 포스터 조회 API
     *
     * @param int page
     * @param int size
     * @return ApiResponse<GetPostersResponse>
     */
    @GetMapping("/my-posters")
    public ApiResponse<GetPostersResponse> getMyPosters(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        try {
            log.info("GetMyPosters request");
            Logging.time();

            List<PosterDto> myPosters = posterService.getMyPosters(page, size).getContent();
            GetPostersResponse result = new GetPostersResponse(myPosters);

            return new ApiResponse<>(ResponseMessage.POSTER_SUCCESS.getCode(), ResponseMessage.POSTER_SUCCESS.getMessage(), result);
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.POSTER_FAILURE.getCode(), e.getMessage());
        }
    }


    /**
     * 포스터 관심 등록 / 해제 API
     *
     * @param Long posterId
     * @param Long isLike
     * @return ApiResponse
     */
    @PostMapping("/like/{posterId}")
    public ApiResponse likePoster(
            @PathVariable("posterId") Long posterId,
            @RequestParam("isLike") Boolean isLike) {
        try {
            log.info("LikePoster request: posterId={}, isLike={}", posterId, isLike);
            Logging.time();

            posterService.likePoster(posterId, isLike);

            return new ApiResponse<>(ResponseMessage.LIKE_SUCCESS.getCode(), ResponseMessage.LIKE_SUCCESS.getMessage());
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.LIKE_FAILURE.getCode(), e.getMessage());
        }
    }


    /**
     * 관심 등록한 포스터 조회 API
     *
     * @param int page
     * @param int size
     * @return ApiResponse<GetPostersResponse>
     */
    @GetMapping("/my-likes")
    public ApiResponse<GetPostersResponse> getLikePosters(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        try {
            log.info("GetLikePosters request");
            Logging.time();

            List<PosterDto> likePosters = posterService.getLikePosters(page, size).getContent();
            GetPostersResponse result = new GetPostersResponse(likePosters);

            return new ApiResponse<>(ResponseMessage.LIKE_GET_POSTER_SUCCESS.getCode(), ResponseMessage.LIKE_GET_POSTER_SUCCESS.getMessage(), result);
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.LIKE_GET_POSTER_FAILURE.getCode(), e.getMessage());
        }
    }
}