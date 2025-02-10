package fete.be.domain.poster.web;

import fete.be.domain.event.exception.InvalidMoodLengthException;
import fete.be.domain.event.exception.NotFoundMoodException;
import fete.be.domain.member.application.MemberService;
import fete.be.domain.member.exception.GuestUserException;
import fete.be.domain.poster.application.PosterService;
import fete.be.domain.poster.application.dto.request.*;
import fete.be.domain.poster.application.dto.response.GetPostersResponse;
import fete.be.domain.poster.application.dto.response.PosterDto;
import fete.be.domain.poster.exception.ProfileImageCountExceedException;
import fete.be.global.util.ApiResponse;
import fete.be.global.util.Logging;
import fete.be.global.util.ResponseMessage;
import fete.be.global.util.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posters")
public class PosterController {

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

        try {
            // 포스터 등록 실행
            Long savedPosterId = posterService.writePoster(request);

            return new ApiResponse<>(ResponseMessage.POSTER_SUCCESS.getCode(), ResponseMessage.POSTER_SUCCESS.getMessage());
        } catch (ProfileImageCountExceedException e) {
            return new ApiResponse<>(ResponseMessage.POSTER_FAILURE.getCode(), e.getMessage());
        } catch (InvalidMoodLengthException e) {
            return new ApiResponse<>(ResponseMessage.POSTER_FAILURE.getCode(), e.getMessage());
        } catch (NotFoundMoodException e) {
            return new ApiResponse<>(ResponseMessage.POSTER_FAILURE.getCode(), e.getMessage());
        }
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
        log.info("ModifyPoster request: posterId={}, request={}", posterId, request);
        Logging.time();

        try {
            // posterId로 포스터를 찾아 수정사항 업데이트
            Long updatePosterId = posterService.updatePoster(posterId, request);
            return new ApiResponse<>(ResponseMessage.POSTER_SUCCESS.getCode(), ResponseMessage.POSTER_SUCCESS.getMessage());

        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.POSTER_FAILURE.getCode(), e.getMessage());
        } catch (URISyntaxException e) {
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
        } catch (URISyntaxException e) {
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

        try {
            Page<PosterDto> pageInfo = posterService.getPosters(findStatus, page, size);
            GetPostersResponse result = new GetPostersResponse(pageInfo);

            return new ApiResponse<>(ResponseMessage.POSTER_SUCCESS.getCode(), ResponseMessage.POSTER_SUCCESS.getMessage(), result);
        } catch (GuestUserException e) {
            // 게스트용 포스터 전체 조회 메서드 실행
            Page<PosterDto> pageInfo = posterService.getGuestPosters(findStatus, page, size);
            GetPostersResponse result = new GetPostersResponse(pageInfo);

            return new ApiResponse<>(ResponseMessage.POSTER_SUCCESS.getCode(), ResponseMessage.POSTER_SUCCESS.getMessage(), result);
        } catch (Exception e) {
            return new ApiResponse<>(ResponseMessage.POSTER_FAILURE.getCode(), e.getMessage());
        }
    }


    /**
     * 포스터 전체 조회 (필터링) API
     */
    @PostMapping("/filter")
    public ApiResponse<GetPostersResponse> getPostersWithFilters(
            @RequestBody Filter request,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        log.info("GetPostersWithFilters request");
        Logging.time();

        try {
            // 필터링 포스터 조회
            Page<PosterDto> pageInfo = posterService.getPostersWithFilters(page, size, request);
            GetPostersResponse result = new GetPostersResponse(pageInfo);

            return new ApiResponse<>(ResponseMessage.POSTER_SUCCESS.getCode(), ResponseMessage.POSTER_SUCCESS.getMessage(), result);
        } catch (GuestUserException e) {
            // 게스트용 필터링 포스터 조회
            Page<PosterDto> pageInfo = posterService.getGuestPostersWithFilters(page, size, request);
            GetPostersResponse result = new GetPostersResponse(pageInfo);

            return new ApiResponse<>(ResponseMessage.POSTER_SUCCESS.getCode(), ResponseMessage.POSTER_SUCCESS.getMessage(), result);
        } catch (Exception e) {
            return new ApiResponse<>(ResponseMessage.POSTER_FAILURE.getCode(), e.getMessage());
        }
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
            @RequestParam(name = "status", defaultValue = "ACTIVE") String status
    ) {
        log.info("GetPoster request: posterId={}, status={}", posterId, status);
        Logging.time();

        // status를 Status enum 타입으로 변환
        Status findStatus = Status.valueOf(status);

        try {
            PosterDto result = posterService.getPoster(posterId, findStatus);
            return new ApiResponse<>(ResponseMessage.POSTER_SUCCESS.getCode(), ResponseMessage.POSTER_SUCCESS.getMessage(), result);
        } catch (GuestUserException e) {
            // 게스트용 포스터 전체 조회 메서드 실행
            PosterDto result = posterService.getGuestPoster(posterId, findStatus);
            return new ApiResponse<>(ResponseMessage.POSTER_SUCCESS.getCode(), ResponseMessage.POSTER_SUCCESS.getMessage(), result);
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.POSTER_NO_EXIST.getCode(), e.getMessage());
        }
    }


    /**
     * 내가 등록한 포스터 조회 API
     *
     * @param int page
     * @param int size
     * @return ApiResponse<GetPostersResponse>
     */
    @PostMapping("/my-posters")
    public ApiResponse<GetPostersResponse> getMyPosters(
            @RequestBody MyPosterFilter request,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        try {
            log.info("GetMyPosters request");
            Logging.time();

            Page<PosterDto> pageInfo = posterService.getMyPosters(page, size, request);
            GetPostersResponse result = new GetPostersResponse(pageInfo);

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

            Page<PosterDto> pageInfo = posterService.getLikePosters(page, size);
            GetPostersResponse result = new GetPostersResponse(pageInfo);

            return new ApiResponse<>(ResponseMessage.LIKE_GET_POSTER_SUCCESS.getCode(), ResponseMessage.LIKE_GET_POSTER_SUCCESS.getMessage(), result);
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.LIKE_GET_POSTER_FAILURE.getCode(), e.getMessage());
        }
    }


    /**
     * 포스터 검색 API
     * - 키워드로 포스터 제목, 이벤트 설명 필드를 검색하는 API입니다.
     *
     * @param SearchPostersRequest request
     * @param int                  page
     * @param int                  size
     * @return ApiResponse<GetPostersResponse>
     */
    @PostMapping("/search")
    public ApiResponse<GetPostersResponse> searchPosters(
            @RequestBody SearchPostersRequest request,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        log.info("SearchPosters request: request={}", request);
        Logging.time();

        // 키워드 추출
        String keyword = request.getKeyword();

        try {
            // 제목 또는 설명에 키워드가 포함되어 있는 포스터들 조회
            Page<PosterDto> pageInfo = posterService.searchPosters(keyword, page, size);
            GetPostersResponse result = new GetPostersResponse(pageInfo);

            return new ApiResponse<>(ResponseMessage.POSTER_SEARCH_SUCCESS.getCode(), ResponseMessage.POSTER_SEARCH_SUCCESS.getMessage(), result);
        } catch (GuestUserException e) {
            // 게스트용 검색 메서드 실행
            Page<PosterDto> pageInfo = posterService.searchGuestPosters(keyword, page, size);
            GetPostersResponse result = new GetPostersResponse(pageInfo);

            return new ApiResponse<>(ResponseMessage.POSTER_SEARCH_SUCCESS.getCode(), ResponseMessage.POSTER_SEARCH_SUCCESS.getMessage(), result);
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.POSTER_SEARCH_FAILURE.getCode(), e.getMessage());
        }
    }
}