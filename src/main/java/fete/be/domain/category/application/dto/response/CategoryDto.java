package fete.be.domain.category.application.dto.response;

import fete.be.domain.admin.application.dto.response.SimplePosterDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {
    private Long categoryId;
    private String categoryName;
    private List<SimplePosterDto> simplePosters;
}
