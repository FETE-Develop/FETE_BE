package fete.be.domain.category.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
public class GetCategoriesResponse {
    private List<CategoryDto> categories;
}
