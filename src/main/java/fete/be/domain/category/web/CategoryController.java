package fete.be.domain.category.web;

import fete.be.domain.category.application.CategoryService;
import fete.be.domain.category.application.dto.response.CategoryDto;
import fete.be.domain.category.application.dto.response.EndedCategoryResponse;
import fete.be.domain.category.application.dto.response.GetCategoriesResponse;
import fete.be.domain.member.exception.GuestUserException;
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
@RequestMapping("/api/categories")
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;


    /**
     * 카테고리 전체 조회 API
     *
     * @return ApiResponse<GetCategoriesResponse>
     */
    @GetMapping
    public ApiResponse<GetCategoriesResponse> getCategories() {
        try {
            // 카테고리 전체 조회 (페이징 없이)
            List<CategoryDto> categories = categoryService.getCategories();
            GetCategoriesResponse result = new GetCategoriesResponse(categories);

            return new ApiResponse<>(ResponseMessage.CATEGORY_GET_CATEGORIES.getCode(), ResponseMessage.CATEGORY_GET_CATEGORIES.getMessage(), result);
        } catch (GuestUserException e) {
            // 게스트용 카테고리 전체 조회 (페이징 없이)
            List<CategoryDto> categories = categoryService.getGuestCategories();
            GetCategoriesResponse result = new GetCategoriesResponse(categories);

            return new ApiResponse<>(ResponseMessage.CATEGORY_GET_CATEGORIES.getCode(), ResponseMessage.CATEGORY_GET_CATEGORIES.getMessage(), result);
        }
    }


    /**
     * 종료된 이벤트 카테고리 조회 API
     * - 7일 이내로 종료된 프스터만 조회
     *
     * @return ApiResponse<EndedCategoryResponse>
     */
    @GetMapping("/end")
    public ApiResponse<EndedCategoryResponse> getEndedCategory() {
        try {
            // 종료된 이벤트 카테고리 조회 (페이징 X)
            EndedCategoryResponse result = categoryService.getEndedCategory();

            return new ApiResponse<>(ResponseMessage.GET_END_CATEGORY.getCode(), ResponseMessage.GET_END_CATEGORY.getMessage(), result);
        } catch (GuestUserException e) {
            // 게스트용 종료된 이벤트 카테고리 조회 (페이징 X)
            EndedCategoryResponse result = categoryService.getGuestEndedCategory();

            return new ApiResponse<>(ResponseMessage.GET_END_CATEGORY.getCode(), ResponseMessage.GET_END_CATEGORY.getMessage(), result);
        }
    }
}
