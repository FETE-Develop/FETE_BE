package fete.be.domain.member.oauth.kakao;

import fete.be.domain.member.application.dto.request.LoginRequestDto;
import fete.be.domain.member.application.dto.request.SignupRequestDto;
import fete.be.domain.member.oauth.kakao.dto.KakaoSignUpRequest;
import fete.be.domain.member.oauth.kakao.dto.KakaoUserInfoResponse;
import fete.be.domain.member.oauth.kakao.exception.KakaoUserNotFoundException;
import fete.be.domain.member.persistence.Member;
import fete.be.domain.member.persistence.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KakaoAuthService {

    private final KakaoUserInfo kakaoUserInfo;
    private final MemberRepository memberRepository;

    /**
     * 카카오 로그인 로직
     * 1. 프론트가 카카오 로그인 과정의 Step1, Step2를 통해 accessToken을 발급 받는다.
     * 2. 백엔드는 받는 토큰을 카카오 서버로 보내 유저 정보를 요청한다.
     * 3. 유저 정보의 email 값을 통해 회원가입 여부를 판단한다.
     *    - 이미 회원일 경우, email을 리턴
     *    - 새로운 회원일 경우, 회원가입 진행 후 email을 리턴
     *      -> OAuth로 회원가입 할 때는, id를 email로 password를 카카오 유저정보의 고유 id로 등록
     * 4. id와 password를 통해 login을 호출하고 jwt 토큰을 발행해서 프론트로 전달
     */


    /**
     * 프론트에서 받은 accessToken을 카카오 서버로 보내 유저 정보를 받아오는 메서드
     * - userInfo에서 email을 확인하여 현재 백엔드 DB에 회원 정보가 있는지 확인
     * - 회원 정보가 있다면 email을 리턴, 없다면 Exception throw
     *
     * @param String accessToken
     * @return String email
     */
    public LoginRequestDto checkSignUp(String accessToken) {
        // 카카오로 accessToken을 보내 회원정보를 받아오기
        KakaoUserInfoResponse userInfo = kakaoUserInfo.getUserInfo(accessToken);
        Member member = memberRepository.findByEmail(userInfo.getKakao_account().getEmail()).orElseThrow(
                () -> new KakaoUserNotFoundException("해당 회원이 존재하지 않습니다.", userInfo)
        );

        LoginRequestDto loginInfo = new LoginRequestDto(member.getEmail(), member.getPassword());
        return loginInfo;
    }

    /**
     * 카카오 계정으로 신규 로그인할 경우
     * - 카카오에서 받은 계정 정보와 프론트에서 입력 받은 사용자 정보로 회원가입 하기
     */
    public SignupRequestDto createSignUpDto(KakaoUserInfoResponse userInfo, KakaoSignUpRequest request) {
        SignupRequestDto signupRequestDto = new SignupRequestDto(userInfo, request);
        return signupRequestDto;
    }

}
