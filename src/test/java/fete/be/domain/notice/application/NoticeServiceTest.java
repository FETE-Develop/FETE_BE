package fete.be.domain.notice.application;

import fete.be.domain.admin.application.dto.request.CreateNoticeRequest;
import fete.be.domain.admin.application.dto.request.ModifyNoticeRequest;
import fete.be.domain.member.application.MemberService;
import fete.be.domain.member.application.dto.request.SignupRequestDto;
import fete.be.domain.member.persistence.Gender;
import fete.be.domain.notice.application.dto.GetNoticeResponse;
import fete.be.domain.notice.application.dto.SimpleNotice;
import fete.be.domain.notice.persistence.Notice;
import fete.be.domain.notice.persistence.NoticeRepository;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class NoticeServiceTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private NoticeService noticeService;
    @Autowired
    private NoticeRepository noticeRepository;


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

    @DisplayName("공지사항 등록")
    @Test
    void 성공_공지사항_등록() {
        // given
        CreateNoticeRequest createNoticeRequest = new CreateNoticeRequest("공지사항 제목", "공지사항 내용");

        // when - 공지사항 등록
        Long createdNoticeId = noticeService.createNotice(createNoticeRequest);
        Optional<Notice> notice = noticeRepository.findById(createdNoticeId);

        // then
        assertThat(notice).isPresent();
    }

    @DisplayName("공지사항 수정")
    @Test
    void 성공_공지사항_수정() {
        // given - 공지사항 등록
        CreateNoticeRequest createNoticeRequest = new CreateNoticeRequest("공지사항 제목", "공지사항 내용");
        Long createdNoticeId = noticeService.createNotice(createNoticeRequest);

        // when - 공지사항 수정
        ModifyNoticeRequest modifyNoticeRequest = new ModifyNoticeRequest("수정된 제목", "수정된 내용");
        Long modifiedNoticeId = noticeService.modifyNotice(createdNoticeId, modifyNoticeRequest);
        Optional<Notice> notice = noticeRepository.findById(modifiedNoticeId);

        // then
        assertThat(createdNoticeId).isEqualTo(modifiedNoticeId);
        assertThat(notice.get().getTitle()).isEqualTo("수정된 제목");
    }

    @DisplayName("공지사항 삭제 (하드 삭제)")
    @Test
    void 성공_공지사항_삭제() {
        // given - 공지사항 등록
        CreateNoticeRequest createNoticeRequest = new CreateNoticeRequest("공지사항 제목", "공지사항 내용");
        Long createdNoticeId = noticeService.createNotice(createNoticeRequest);

        // when - 공지사항 삭제
        noticeService.deleteNotice(createdNoticeId);
        Optional<Notice> notice = noticeRepository.findById(createdNoticeId);

        // then
        assertThat(notice).isNotPresent();
    }

    @DisplayName("공지사항 전체 목록 조회")
    @Test
    void 성공_공지사항_전체조회() {
        // given - 공지사항 2개 등록
        Long noticeId1 = noticeService.createNotice(new CreateNoticeRequest("공지사항 1", "1번 공지사항"));
        Long noticeId2 = noticeService.createNotice(new CreateNoticeRequest("공지사항 2", "2번 공지사항"));

        // when - 공지사항 전체 목록 조회
        List<SimpleNotice> notices = noticeService.getNotices(0, 10).getContent();

        // then
        assertThat(notices.size()).isEqualTo(2);
        assertThat(notices.get(0).getTitle()).isEqualTo("공지사항 1");
        assertThat(notices.get(1).getTitle()).isEqualTo("공지사항 2");
    }

    @DisplayName("공지사항 단건 조회")
    @Test
    void 성공_공지사항_단건조회() {
        // given - 공지사항 등록
        Long noticeId = noticeService.createNotice(new CreateNoticeRequest("공지사항 1", "1번 공지사항"));

        // when - 공지사항 단건 조회
        GetNoticeResponse notice = noticeService.getNotice(noticeId);

        // then
        assertThat(notice).isNotNull();
        assertThat(notice.getTitle()).isEqualTo("공지사항 1");
        assertThat(notice.getContent()).isEqualTo("1번 공지사항");
    }
}