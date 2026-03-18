package fete.be.domain.category.application;

import fete.be.domain.admin.application.dto.response.SimplePosterDto;
import fete.be.domain.category.application.dto.response.CategoryDto;
import fete.be.domain.category.application.dto.response.CategoryFlatDto;
import fete.be.domain.category.application.dto.response.EndedCategoryResponse;
import fete.be.domain.category.persistence.CategoryQueryMapper;
import fete.be.domain.member.application.MemberService;
import fete.be.domain.member.persistence.Member;
import fete.be.domain.poster.persistence.PosterQueryMapper;
import fete.be.global.util.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CategoryQueryService {

    private final MemberService memberService;
    private final PosterQueryMapper posterQueryMapper;
    private final CategoryQueryMapper categoryQueryMapper;


    public List<CategoryDto> getCategories() {
        // 유저 조회
        Member member = memberService.findMemberByEmail();

        // 카테고리 관련 모든 데이터 조회
        List<CategoryFlatDto> flatCategories = categoryQueryMapper.findActiveCategoryPosters(member.getMemberId());
        Map<Long, CategoryDto> categories = new LinkedHashMap<>();

        // 카테고리 그룹핑 진행
        for (CategoryFlatDto dto : flatCategories) {
            // 카테고리 생성 (중복 방지 로직)
            categories.putIfAbsent(
                    dto.getCategoryId(),
                    new CategoryDto(dto.getCategoryId(), dto.getCategoryName(), new ArrayList<>())
            );

            // 카테고리에 맞게 포스터 추가
            categories.get(dto.getCategoryId()).getSimplePosters().add(
                    new SimplePosterDto(
                            dto.getPosterId(),
                            dto.getEventName(),
                            dto.getPosterImage(),
                            dto.getManager(),
                            dto.getStartDate(),
                            dto.getEndDate(),
                            dto.getAddress(),
                            dto.getSimpleAddress(),
                            dto.getMoods(),
                            dto.getGenres(),
                            dto.getIsLike()
                    )
            );
        }

        return new ArrayList<>(categories.values());
    }

    public List<CategoryDto> getGuestCategories() {
        // 카테고리 관련 모든 데이터 조회
        List<CategoryFlatDto> flatCategories = categoryQueryMapper.findGuestActiveCategoryPosters();
        Map<Long, CategoryDto> categories = new LinkedHashMap<>();

        // 카테고리 그룹핑 진행
        for (CategoryFlatDto dto : flatCategories) {
            // 카테고리 생성 (중복 방지 로직)
            categories.putIfAbsent(
                    dto.getCategoryId(),
                    new CategoryDto(dto.getCategoryId(), dto.getCategoryName(), new ArrayList<>())
            );

            // 카테고리에 맞게 포스터 추가
            categories.get(dto.getCategoryId()).getSimplePosters().add(
                    new SimplePosterDto(
                            dto.getPosterId(),
                            dto.getEventName(),
                            dto.getPosterImage(),
                            dto.getManager(),
                            dto.getStartDate(),
                            dto.getEndDate(),
                            dto.getAddress(),
                            dto.getSimpleAddress(),
                            dto.getMoods(),
                            dto.getGenres(),
                            dto.getIsLike()
                    )
            );
        }

        return new ArrayList<>(categories.values());
    }

    public EndedCategoryResponse getEndedCategory() {
        // 유저 조회
        Member member = memberService.findMemberByEmail();

        // 날짜 계산
        LocalDateTime today = LocalDate.now().atTime(0, 0, 0);
        LocalDateTime tomorrow = today.plusDays(1);
        LocalDateTime sevenDaysAgo = today.minusDays(7);

        // 오늘 날짜 포함 7일 이내로 종료된 포스터 조회
        List<SimplePosterDto> endedPosters = posterQueryMapper.findEndedPosters(
                member.getMemberId(),
                sevenDaysAgo,
                tomorrow
        );

        return new EndedCategoryResponse("종료된 이벤트", endedPosters);
    }

    public EndedCategoryResponse getGuestEndedCategory() {
        // 날짜 계산
        LocalDateTime today = LocalDate.now().atTime(0, 0, 0);
        LocalDateTime tomorrow = today.plusDays(1);
        LocalDateTime sevenDaysAgo = today.minusDays(7);

        // 오늘 날짜 포함 7일 이내로 종료된 포스터 조회
        List<SimplePosterDto> endedPosters = posterQueryMapper.findGuestEndedPosters(sevenDaysAgo, tomorrow);

        return new EndedCategoryResponse("종료된 이벤트", endedPosters);
    }
}
