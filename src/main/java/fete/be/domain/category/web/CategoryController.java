package fete.be.domain.category.web;

import fete.be.domain.category.application.CategoryService;
import fete.be.domain.category.application.dto.response.CategoryDto;
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
        log.info("GetCategories API");
        Logging.time();

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

}
