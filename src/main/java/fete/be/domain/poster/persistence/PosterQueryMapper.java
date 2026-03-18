package fete.be.domain.poster.persistence;

import fete.be.domain.admin.application.dto.response.SimplePosterDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PosterQueryMapper {

    List<SimplePosterDto> findEndedPosters(
            @Param("memberId") Long memberId,
            @Param("sevenDaysAgo")LocalDateTime sevenDaysAgo,
            @Param("tomorrow") LocalDateTime tomorrow
    );

    List<SimplePosterDto> findGuestEndedPosters(
            @Param("sevenDaysAgo")LocalDateTime sevenDaysAgo,
            @Param("tomorrow") LocalDateTime tomorrow
    );
}
