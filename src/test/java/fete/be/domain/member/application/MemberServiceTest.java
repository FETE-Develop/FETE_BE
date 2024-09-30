package fete.be.domain.member.application;

import fete.be.domain.member.application.dto.request.ModifyRequestDto;
import fete.be.domain.member.application.dto.request.SignupRequestDto;
import fete.be.domain.member.exception.BlockedUserException;
import fete.be.domain.member.exception.DuplicateEmailException;
import fete.be.domain.member.exception.DuplicatePhoneNumberException;
import fete.be.domain.member.persistence.Gender;
import fete.be.domain.member.persistence.Member;
import fete.be.domain.member.persistence.MemberRepository;
import fete.be.global.jwt.JwtToken;
import jakarta.validation.ConstraintViolationException;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Nested
    @DisplayName("회원가입")
    class SignUp {

        @DisplayName("회원가입 성공")
        @Test
        void 회원가입_성공() {
            // given
            SignupRequestDto signupRequestDto = new SignupRequestDto("kky6335@gmail.com", "qwer1234!",
                    "www.profile.image.com", "강건영", "강건영입니다.", "2000-11-30", Gender.MALE, "01020856335");

            // when
            memberService.signUp(signupRequestDto);
            Optional<Member> member = memberRepository.findByEmail(signupRequestDto.getEmail());

            // then
            assertThat(member.get()).isInstanceOf(Member.class);
        }


        @DisplayName("예외사항 - 이메일 중복")
        @Test
        void 예외사항_이메일_중복() {
            // given
            SignupRequestDto dto1 = new SignupRequestDto("kky6335@gmail.com", "qwer1234!",
                    "www.profile.image.com", "강건영", "강건영입니다.", "2000-11-30", Gender.MALE, "01020856335");

            SignupRequestDto dto2 = new SignupRequestDto("kky6335@gmail.com", "1234qwer!",
                    "www.profile2.image.com", "영건강", "영건강입니다..", "2000-12-30", Gender.FEMALE, "01012345678");

            // when & then
            memberService.signUp(dto1);
            assertThrows(DuplicateEmailException.class, () -> {
                memberService.signUp(dto2);
            });
        }

        @DisplayName("예외사항 - 휴대전화 번호 중복")
        @Test
        void 예외사항_휴대전화_중복() {
            // given
            SignupRequestDto dto1 = new SignupRequestDto("kky6335@gmail.com", "qwer1234!",
                    "www.profile.image.com", "강건영", "강건영입니다.", "2000-11-30", Gender.MALE, "01020856335");

            SignupRequestDto dto2 = new SignupRequestDto("kgy6335@gmail.com", "1234qwer!",
                    "www.profile2.image.com", "영건강", "영건강입니다..", "2000-12-30", Gender.FEMALE, "01020856335");

            // when & then
            memberService.signUp(dto1);
            assertThrows(DuplicatePhoneNumberException.class, () -> {
                memberService.signUp(dto2);
            });
        }

        @DisplayName("예외사항 - 잘못된 비밀번호 형식(특수기호 미포함)")
        @Test
        void 예외사항_비밀번호_형식() {
            // given
            SignupRequestDto dto1 = new SignupRequestDto("kky6335@gmail.com", "qwer1234",
                    "www.profile.image.com", "강건영", "강건영입니다.", "2000-11-30", Gender.MALE, "01020856335");

            // when & then
            assertThrows(ConstraintViolationException.class, () -> {
                memberService.signUp(dto1);
            });
        }

        @DisplayName("예외사항 - 잘못된 휴대전화 번호 형식(하이픈(-) 기호를 포함해선 안됨)")
        @Test
        void 예외사항_휴대전화_번호_형식() {
            // given
            SignupRequestDto dto1 = new SignupRequestDto("kky6335@gmail.com", "qwer1234!",
                    "www.profile.image.com", "강건영", "강건영입니다.", "2000-11-30", Gender.MALE, "010-2085-6335");

            // when & then
            assertThrows(ConstraintViolationException.class, () -> {
                memberService.signUp(dto1);
            });
        }

        @DisplayName("예외사항 - 잘못된 생일 형식(yyyy-MM-dd 형식을 지켜야 됨)")
        @Test
        void 예외사항_생일_형식() {
            // given
            SignupRequestDto dto1 = new SignupRequestDto("kky6335@gmail.com", "qwer1234!",
                    "www.profile.image.com", "강건영", "강건영입니다.", "2000/11/30", Gender.MALE, "01020856335");

            // when & then
            assertThrows(ConstraintViolationException.class, () -> {
                memberService.signUp(dto1);
            });
        }

        @DisplayName("예외사항 - 차단된 유저의 가입")
        @Test
        void 예외사항_차단된_유저() {
            // given
            SignupRequestDto signupRequestDto = new SignupRequestDto("kky6335@gmail.com", "qwer1234!",
                    "www.profile.image.com", "강건영", "강건영입니다.", "2000-11-30", Gender.MALE, "01020856335");

            // when : 회원가입 -> 강퇴(=차단) -> 재가입
            memberService.signUp(signupRequestDto);
            Optional<Member> member = memberRepository.findByEmail("kky6335@gmail.com");
            memberService.deactivateMember(member.get().getMemberId());

            // then
            assertThrows(BlockedUserException.class, () -> {
                memberService.signUp(signupRequestDto);
            });
        }
    }

    @Nested
    @DisplayName("로그인")
    class Login {

        @BeforeEach
        void setUp() {
            // 회원가입 실행
            SignupRequestDto signupRequestDto = new SignupRequestDto("kky6335@gmail.com", "qwer1234!",
                    "www.profile.image.com", "강건영", "강건영입니다.", "2000-11-30", Gender.MALE, "01020856335");
            memberService.signUp(signupRequestDto);
        }

        @DisplayName("로그인 성공")
        @Test
        void 로그인_성공() {
            // given
            String id = "kky6335@gmail.com";
            String password = "qwer1234!";

            // when
            JwtToken token = memberService.login(id, password);

            // then
            assertThat(token).isNotNull();
        }

        @DisplayName("로그인 실패")
        @Test
        void 로그인_실패() {
            // given
            String id = "kky6335@gmail.com";
            String password = "1234qwer!";

            // when & then
            assertThrows(BadCredentialsException.class, () -> {
                JwtToken token = memberService.login(id, password);
            });
        }
    }

    @Nested
    @DisplayName("유저 프로필 수정")
    class Modify {

        @BeforeEach
        void setUp() {
            // SecurityContext에 인증정보 임의로 설정
            String tempEmail = "kky6335@gmail.com";
            String tempPassword = "qwer1234!";

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(tempEmail, tempPassword, List.of());
            SecurityContext securityContext = new SecurityContextImpl(authentication);
            SecurityContextHolder.setContext(securityContext);

            // 계정 생성
            SignupRequestDto signupRequestDto = new SignupRequestDto("kky6335@gmail.com", "qwer1234!",
                    "www.profile.image.com", "강건영", "강건영입니다.", "2000-11-30", Gender.MALE, "01020856335");
            memberService.signUp(signupRequestDto);
        }

        @Test
        @DisplayName("유저 프로필 수정 성공")
        void 프로필_수정_성공() {
            // given
            ModifyRequestDto modifyDto = new ModifyRequestDto("www.newProfile.image.com", "강하원", "강하원입니다.", "2000-12-30", Gender.FEMALE, "01012345678");

            // when
            Long modifiedMemberId = memberService.modify(modifyDto);

            // then
            Member member = memberRepository.findById(modifiedMemberId).orElseThrow();
            assertThat(member.getUserName()).isEqualTo("강하원");
            assertThat(member.getBirth()).isEqualTo("2000-12-30");
        }
    }

    @Nested
    @DisplayName("유저 탈퇴")
    class Deactivate {

        @BeforeEach
        void setUp() {
            // SecurityContext에 인증정보 임의로 설정
            String tempEmail = "kky6335@gmail.com";
            String tempPassword = "qwer1234!";

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(tempEmail, tempPassword, List.of());
            SecurityContext securityContext = new SecurityContextImpl(authentication);
            SecurityContextHolder.setContext(securityContext);

            // 계정 생성
            SignupRequestDto signupRequestDto = new SignupRequestDto("kky6335@gmail.com", "qwer1234!",
                    "www.profile.image.com", "강건영", "강건영입니다.", "2000-11-30", Gender.MALE, "01020856335");
            memberService.signUp(signupRequestDto);
        }

        @Test
        @DisplayName("유저 탈퇴 성공")
        void 유저_탈퇴_성공() {
            // given
            Member currentMember = memberService.findMemberByEmail();

            // when
            memberService.deactivateMember();

            // then
            Optional<Member> deletedMember = memberRepository.findByEmail(currentMember.getEmail());
            assertThat(deletedMember).isNotPresent();
        }
    }

    @Nested
    @DisplayName("유저 편의 기능")
    class FindUserInfo {

    }
}