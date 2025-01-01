package fete.be.domain.category.application;

import fete.be.domain.admin.application.dto.request.ApprovePostersRequest;
import fete.be.domain.admin.application.dto.request.CreateCategoryRequest;
import fete.be.domain.admin.application.dto.request.ModifyCategoryRequest;
import fete.be.domain.admin.application.dto.response.AccountDto;
import fete.be.domain.category.application.dto.response.CategoryDto;
import fete.be.domain.category.persistence.Category;
import fete.be.domain.category.persistence.CategoryRepository;
import fete.be.domain.event.persistence.ArtistDto;
import fete.be.domain.event.persistence.TicketInfoDto;
import fete.be.domain.member.application.MemberService;
import fete.be.domain.member.application.dto.request.SignupRequestDto;
import fete.be.domain.member.persistence.Gender;
import fete.be.domain.poster.application.PosterService;
import fete.be.domain.poster.application.dto.request.EventDto;
import fete.be.domain.poster.application.dto.request.Place;
import fete.be.domain.poster.application.dto.request.WritePosterRequest;
import fete.be.global.util.ResponseMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class CategoryServiceTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private PosterService posterService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryRepository categoryRepository;


    @BeforeEach
    void setUp() {
        // SecurityContext에 인증정보 임의로 설정
        String tempEmail = "kky6335@gmail.com";
        String tempPassword = "hawon1234!";

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(tempEmail, tempPassword, List.of());
        SecurityContext securityContext = new SecurityContextImpl(authentication);
        SecurityContextHolder.setContext(securityContext);

        // 계정 생성
        SignupRequestDto signupRequestDto = new SignupRequestDto("kky6335@gmail.com", "hawon1234!",
                "www.profile.image.com", "강건영", "강건영입니다.", "2000-12-31", Gender.MALE, "01012345678");
        memberService.signUp(signupRequestDto);
    }

    @DisplayName("카테고리 생성")
    @Test
    void 성공_카테고리_생성() {
        // given
        WritePosterRequest writePosterRequest = generateWriteRequest();
        Long posterId = posterService.writePoster(writePosterRequest);

        // when
        CreateCategoryRequest request = new CreateCategoryRequest("HOT Event", List.of(posterId));
        Long categoryId = categoryService.createCategory(request);
        Optional<Category> category = categoryRepository.findById(categoryId);

        // then
        assertThat(category).isPresent();
    }

    @DisplayName("카테고리 수정")
    @Test
    void 성공_카테고리_수정() {
        // given - 카테고리 생성
        WritePosterRequest writePosterRequest = generateWriteRequest();
        Long posterId = posterService.writePoster(writePosterRequest);
        CreateCategoryRequest request = new CreateCategoryRequest("HOT Event", List.of(posterId));
        Long categoryId = categoryService.createCategory(request);

        // when - 카테고리 이름 변경
        ModifyCategoryRequest modifyRequest = new ModifyCategoryRequest("COLD Event", List.of(posterId));
        Long modifiedId = categoryService.modifyCategory(categoryId, modifyRequest);
        Category category = categoryRepository.findById(modifiedId).orElseThrow(
                () -> new IllegalArgumentException(ResponseMessage.CATEGORY_NO_EXIST.getMessage())
        );

        // then
        assertThat(category.getCategoryId()).isEqualTo(modifiedId);
        assertThat(category.getCategoryName()).isEqualTo("COLD Event");
    }

    @DisplayName("카테고리 삭제 (하드 삭제)")
    @Test
    void 성공_카테고리_삭제() {
        // given - 카테고리 생성
        WritePosterRequest writePosterRequest = generateWriteRequest();
        Long posterId = posterService.writePoster(writePosterRequest);
        CreateCategoryRequest request = new CreateCategoryRequest("HOT Event", List.of(posterId));
        Long categoryId = categoryService.createCategory(request);

        // when - 카테고리 삭제
        categoryService.deleteCategory(categoryId);

        // then
        Optional<Category> deletedCategory = categoryRepository.findById(categoryId);
        assertThat(deletedCategory).isNotPresent();
    }

    @DisplayName("카테고리 전체 조회")
    @Test
    void 성공_카테고리_전체조회() {
        // given - 카테고리 생성
        WritePosterRequest writePosterRequest = generateWriteRequest();
        Long posterId = posterService.writePoster(writePosterRequest);
        posterService.approvePosters(new ApprovePostersRequest(List.of(posterId)));

        CreateCategoryRequest request = new CreateCategoryRequest("HOT Event", List.of(posterId));
        Long categoryId = categoryService.createCategory(request);

        // when - 카테고리 삭제
        List<CategoryDto> categories = categoryService.getCategories();

        // then
        assertThat(categories.size()).isEqualTo(1);
        assertThat(categories.get(0).getCategoryName()).isEqualTo("HOT Event");
    }


    /**
     * 포스터 등록 DTO 객체 생성
     *
     * @return WritePosterRequest
     */
    WritePosterRequest generateWriteRequest() {
        // 장소 정보 생성
        Place place = new Place(
                "경기도 화성시 병점중앙로16 101동 106호",
                "병점동",
                "2층",
                41.01234,
                39.1234
        );

        // 티켓 정보 생성
        List<TicketInfoDto> tickets = Arrays.asList(
                new TicketInfoDto("얼리버드", "얼리버드는 티켓의 간단한 소개 내용입니다.", 10000, 10),
                new TicketInfoDto("현장구매", "현장구매는 현장에서 구매하셔야 합니다.", 0, 8)
        );

        // 아티스트 정보 생성
        List<ArtistDto> artists = Arrays.asList(
                new ArtistDto("이하이", "https://info.url", ""),
                new ArtistDto("악동뮤지션", "https://info.url", "")
        );

        // 계좌 정보 생성
        AccountDto account = new AccountDto(
                "농협",
                "20708012341234",
                "강건영"
        );

        // 이벤트 정보 생성
        EventDto event = new EventDto(
                "FETE의 첫 이벤트",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(3),
                place,
                false,
                false,
                false,
                tickets,
                "이벤트 관련 추가 설명입니다",
                "몽환적인,잔잔한",
                "POP,DISCO",
                "http://www.instargram.kr",
                artists,
                account
        );

        // 포스터 등록 DTO 생성
        WritePosterRequest request = new WritePosterRequest(
                new String[]{
                        "https://picsum.photos/230/530",
                        "https://picsum.photos/300/500",
                        "https://picsum.photos/1080/2080",
                        "https://picsum.photos/1080/400"
                },
                "FETE",
                "강건영",
                "010-1234-5678",
                event
        );

        return request;
    }
}