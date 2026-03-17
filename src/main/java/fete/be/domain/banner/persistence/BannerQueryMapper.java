package fete.be.domain.banner.persistence;

import fete.be.domain.banner.application.dto.response.BannerDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BannerQueryMapper {
    List<BannerDto> findActiveBanners();
}
