package fete.be.domain.admin.application.dto.request;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class CreateCategoryRequest {
    private String categoryName;  // 카테고리 이름
    private List<Long> posterIds;  // 카테고리에 들어갈 포스터 아이디들
}
