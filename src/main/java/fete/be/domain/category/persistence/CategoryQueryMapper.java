package fete.be.domain.category.persistence;

import fete.be.domain.category.application.dto.response.CategoryFlatDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryQueryMapper {

    List<CategoryFlatDto> findActiveCategoryPosters(@Param("memberId") Long memberId);
    List<CategoryFlatDto> findGuestActiveCategoryPosters();
}
