package fete.be.domain.category.persistence;

import fete.be.domain.admin.application.dto.request.CreateCategoryRequest;
import fete.be.domain.admin.application.dto.request.ModifyCategoryRequest;
import fete.be.domain.poster.application.PosterService;
import fete.be.domain.poster.persistence.Poster;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "category_name")
    private String categoryName;

    @OneToMany(mappedBy = "category", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Poster> posters = new ArrayList<>();


    // 생성 메서드
    public static Category createCategory(CreateCategoryRequest request, PosterService posterService) {
        Category category = new Category();
        category.categoryName = request.getCategoryName();

        // posterId로 포스터 조회하여 리스트에 담기
        List<Long> posterIds = request.getPosterIds();
        for (Long posterId : posterIds) {
            Poster poster = posterService.findPosterByPosterId(posterId);
            category.posters.add(poster);
            poster.setCategory(category);  // 양방향 매핑
        }

        return category;
    }

    // 수정 메서드
    public static Category modifyCategory(Category category, ModifyCategoryRequest request, PosterService posterService) {
        category.categoryName = request.getCategoryName();

        // 연결된 포스터들 삭제
        for (Poster poster : category.posters) {
            poster.setCategory(null);
        }
        category.posters.clear();

        // 요청으로 들어온 포스터들로 재설정
        List<Long> posterIds = request.getPosterIds();
        for (Long posterId : posterIds) {
            Poster poster = posterService.findPosterByPosterId(posterId);
            category.posters.add(poster);
            poster.setCategory(category);  // 양방향 매핑
        }

        return category;
    }

    // 삭제 메서드
    public static void deleteCategory(Category category) {
        // 연결된 포스터들 삭제
        for (Poster poster : category.posters) {
            poster.setCategory(null);
        }
    }
}
