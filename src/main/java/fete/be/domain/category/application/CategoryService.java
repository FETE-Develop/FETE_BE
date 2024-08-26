package fete.be.domain.category.application;

import fete.be.domain.admin.application.dto.request.CreateCategoryRequest;
import fete.be.domain.category.persistence.Category;
import fete.be.domain.category.persistence.CategoryRepository;
import fete.be.domain.poster.application.PosterService;
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
        Category category = Category.createCategory(request, posterService);
        categoryRepository.save(category);

        return category.getCategoryId();
    }
}
