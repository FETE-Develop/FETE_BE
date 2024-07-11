package fete.be.domain.member.application;

import fete.be.domain.member.application.dto.request.GrantAdminRequestDto;
import fete.be.domain.member.application.dto.request.SignupRequestDto;
import fete.be.domain.member.persistence.Member;
import fete.be.domain.member.persistence.MemberRepository;
import fete.be.domain.member.persistence.Role;
import fete.be.global.jwt.JwtProvider;
import fete.be.global.jwt.JwtToken;
import fete.be.global.util.ResponseMessage;
import fete.be.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtProvider jwtProvider;

    @Value("${admin.key}")
    private String adminKey;

    @Transactional
    public void signup(SignupRequestDto request) {
        // email 중복 검사
        if (isDuplicateEmail(request.getEmail())) {
            throw new IllegalArgumentException(ResponseMessage.SIGNUP_DUPLICATE_EMAIL.getMessage());
        }

        // 검증에 성공할 경우
        Member member = Member.createMember(request.getEmail(), request.getPassword(), request.getUserName());
        memberRepository.save(member);
    }

    @Transactional
    public JwtToken login(String id, String password) {
        // 1. id, password를 기반으로 Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(id, password);

        // 2. 실제 검증이 이루어지는 코드
        // authenticate 메서드가 실행될 때, 우리가 만들어준 CustomUserDetailService에서 만든 loadUserByUsername 메서드가 실행된다.
        // loadUserByUsername 메서드에 우리가 직접 검증 코드를 작성해줘야 한다.
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 검증된 authentication 정보로 JWT 토큰 생성
        JwtToken token = jwtProvider.generateToken(authentication);
        return token;
    }

    public boolean isDuplicateEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    public Member findMemberByEmail() {
        String email = SecurityUtil.getCurrentMemberEmail();
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
        return member;
    }

    @Transactional
    public Long grantAdmin(GrantAdminRequestDto request) {
        Member member = findMemberByEmail();
        String securityCode = request.getSecurityCode();

        // 서버 측 admin 코드와 일치하지 않을 경우
        if (!securityCode.equals(adminKey)) {
            throw new IllegalArgumentException(ResponseMessage.MEMBER_ADMIN_REJECT.getMessage());
        }

        // 이미 ADMIN일 경우
        if (member.getRole().equals(Role.ADMIN)) {
            throw new IllegalArgumentException(ResponseMessage.MEMBER_ADMIN_REJECT.getMessage());
        }

        // 정상 로직 - ADMIN 권한 부여
        Member grantedMember = Member.grantAdmin(member);
        return grantedMember.getMemberId();
    }
}
