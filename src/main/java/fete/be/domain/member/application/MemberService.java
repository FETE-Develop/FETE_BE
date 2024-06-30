package fete.be.domain.member.application;

import fete.be.domain.member.persistence.Member;
import fete.be.domain.member.persistence.MemberRepository;
import fete.be.global.jwt.JwtProvider;
import fete.be.global.jwt.JwtToken;
import fete.be.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtProvider jwtProvider;

    @Transactional
    public void signup(Member member) {
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

}
