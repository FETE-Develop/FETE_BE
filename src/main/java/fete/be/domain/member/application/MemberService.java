package fete.be.domain.member.application;

import fete.be.domain.admin.application.dto.response.MemberDto;
import fete.be.domain.member.application.dto.request.GrantAdminRequestDto;
import fete.be.domain.member.application.dto.request.ModifyRequestDto;
import fete.be.domain.member.application.dto.request.SignupRequestDto;
import fete.be.domain.member.application.dto.response.GetMyProfileResponse;
import fete.be.domain.member.exception.GuestUserException;
import fete.be.domain.member.persistence.*;
import fete.be.global.jwt.JwtProvider;
import fete.be.global.jwt.JwtToken;
import fete.be.global.util.ResponseMessage;
import fete.be.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final BlockedMemberRepository blockedMemberRepository;
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

        // 차단된 유저인지 검사
        boolean isBlocked = blockedMemberRepository.existsByPhoneNumber(request.getPhoneNumber());
        if (isBlocked) {
            throw new IllegalArgumentException(ResponseMessage.MEMBER_BLOCKED.getMessage());
        }

        // 검증에 성공할 경우
        Member member = Member.createMember(request);
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
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new GuestUserException(ResponseMessage.MEMBER_NO_EXIST.getMessage()));
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

    @Transactional
    public Long modify(ModifyRequestDto request) {
        // Member 찾기
        Member member = findMemberByEmail();

        // DTO 값으로 Member 업데이트
        Member modifiedMember = Member.modifyMember(member, request);
        return modifiedMember.getMemberId();
    }

    public Page<MemberDto> getMembers(int page, int size) {
        // 페이징 객체 생성
        Pageable pageable = PageRequest.of(page, size);

        // 페이징 처리된 데이터 반환
        return memberRepository.findAll(pageable)
                .map(member -> new MemberDto(
                        member.getMemberId(),
                        member.getEmail(),
                        member.getProfileImage().getImageUrl(),
                        member.getUserName(),
                        member.getIntroduction(),
                        member.getBirth(),
                        member.getGender(),
                        member.getPhoneNumber(),
                        member.getRole(),
                        member.getCreatedAt(),
                        member.getStatus()));
    }

    @Transactional
    public Long deactivateMember(Long memberId) {
        // 탈퇴할 유저 조회
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalArgumentException(ResponseMessage.MEMBER_NO_EXIST.getMessage())
        );

        // 차단시킬 휴대전화 번호
        String phoneNumber = member.getPhoneNumber();
        BlockedMember blockedMember = BlockedMember.createBlockedMember(phoneNumber);
        BlockedMember savedBlockedMember = blockedMemberRepository.save(blockedMember);

        // DB에서 해당 유저 삭제
        memberRepository.delete(member);

        return savedBlockedMember.getBlockedMemberId();
    }

    public List<String> getAllTokens() {
        List<String> tokens = memberRepository.findByFcmTokenIsNotNull();
        return tokens;
    }

    public GetMyProfileResponse getMyProfile() {
        // 유저 조회
        Member member = findMemberByEmail();
        return new GetMyProfileResponse(member);
    }

    @Transactional
    public void deactivateMember() {
        // 삭제할 회원 조회
        Member member = findMemberByEmail();

        // 회원 정보 삭제
        memberRepository.delete(member);
    }
}
