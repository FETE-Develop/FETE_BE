package fete.be.domain.member.oauth.apple;

import fete.be.domain.member.application.dto.request.LoginRequestDto;
import fete.be.domain.member.application.dto.request.SignupRequestDto;
import fete.be.domain.member.oauth.apple.exception.AppleUserNotFoundException;
import fete.be.domain.member.oauth.apple.dto.AppleSignUpRequest;
import fete.be.domain.member.persistence.Member;
import fete.be.domain.member.persistence.MemberRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AppleAuthService {

    private static final String CLAIM_EMAIL = "email";

    private final AppleTokenParser appleTokenParser;
    private final AppleClient appleClient;
    private final ApplePublicKeyGenerator applePublicKeyGenerator;

    private final MemberRepository memberRepository;

    /**
     * ### 애플 로그인 플로우
     * 1. 애플에서 받아온 Public keys 중에, idToken의 헤더를 Base64 디코딩 하여 얻은 alg, kid 값과 일치하는 Public key를 찾기
     * 2. Public key의 n과 e의 값을 사용하여 KeySpec 생성하기
     * 3. Public Key의 kty 값으로 KeyFactory 생성하기
     * 4. KeySpec과 KeyFactory로 RSA Public Key 생성하기
     * 5. idToken과 RSA Public Key로 Claims를 추출하기
     * 6. 추출한 Claims에서 애플 계정 정보 얻기
     */

    /**
     * Claim 추출 정보
     * - email : 애플 계정 이메일
     * - sub : 애플에서 제공하는 해당 유저의 unique한 id 값 (=OAuthId)
     */
    public AppleUserInfo getAppleUserInfo(String idToken) {
        Map<String, String> appleTokenHeader = appleTokenParser.parseHeader(idToken);
        ApplePublicKeys applePublicKeys = appleClient.getApplePublicKeys();
        PublicKey publicKey = applePublicKeyGenerator.generate(appleTokenHeader, applePublicKeys);
        Claims claims = appleTokenParser.extractClaims(idToken, publicKey);
        return new AppleUserInfo(claims.get(CLAIM_EMAIL, String.class), claims.getSubject());
    }

    /**
     * 애플 계정으로 회원가입의 여부 검사
     */
    public LoginRequestDto checkSignUp(String idToken) {
        // idToken으로 애플 계정 정보 조회
        AppleUserInfo appleUserInfo = getAppleUserInfo(idToken);

        // 애플 이메일이 DB에 존재한다면 기존 회원, 없다면 신규 회원
        Member member = memberRepository.findByEmail(appleUserInfo.getEmail()).orElseThrow(
                () -> new AppleUserNotFoundException("해당 회원이 존재하지 않습니다.", appleUserInfo)
        );

        LoginRequestDto loginInfo = new LoginRequestDto(member.getEmail(), member.getPassword());
        return loginInfo;
    }

    /**
     * 애플 계정 정보와 사용자로부터 입력 받은 정보로 회원가입 DTO 생성
     */
    public SignupRequestDto createSignUpDto(AppleUserInfo appleUserInfo, AppleSignUpRequest request) {
        SignupRequestDto signupRequestDto = new SignupRequestDto(appleUserInfo, request);
        return signupRequestDto;
    }
}
