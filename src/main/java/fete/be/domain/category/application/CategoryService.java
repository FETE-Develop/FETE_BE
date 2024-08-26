package fete.be.domain.category.application;

import fete.be.domain.admin.application.dto.request.CreateCategoryRequest;
import fete.be.domain.admin.application.dto.request.ModifyCategoryRequest;
import fete.be.domain.category.persistence.Category;
import fete.be.domain.category.persistence.CategoryRepository;
import fete.be.domain.poster.application.PosterService;
import fete.be.global.util.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final PosterService posterService;
    private final CategoryRepository categoryRepository;


    @Transactional
    public Long createCategory(CreateCategoryRequest request) {
        // 카테고리 생성 후, 저장
        Category category = Category.createCategory(request, posterService);
        categoryRepository.save(category);

        return category.getCategoryId();
    }

    @Transactional
    public Long modifyCategory(Long categoryId, ModifyCategoryRequest request) {
        // 수정할 카테고리 조회
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new IllegalArgumentException(ResponseMessage.CATEGORY_NO_EXIST.getMessage())
        );

        // 카테고리 수정
        Category modifiedCategory = Category.modifyCategory(category, request, posterService);

        return modifiedCategory.getCategoryId();
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        // 삭제할 카테고리 조회
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new IllegalArgumentException(ResponseMessage.CATEGORY_NO_EXIST.getMessage())
        );

        // 카테고리에 포함되어 있는 포스터들의 카테고리 정보 삭제
        Category.deleteCategory(category);

        // 카테고리 삭제 (Hard 삭제)
        categoryRepository.delete(category);
    }
}
