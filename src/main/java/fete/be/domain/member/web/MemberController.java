package fete.be.domain.member.web;

import fete.be.domain.member.application.MemberService;
import fete.be.domain.member.application.dto.request.GrantAdminRequestDto;
import fete.be.domain.member.application.dto.request.LoginRequestDto;
import fete.be.domain.member.application.dto.request.ModifyRequestDto;
import fete.be.domain.member.application.dto.request.SignupRequestDto;
import fete.be.domain.member.application.dto.response.LoginResponseDto;
import fete.be.domain.member.application.dto.response.SocialLoginResponse;
import fete.be.domain.member.exception.KakaoUserNotFoundException;
import fete.be.domain.member.oauth.kakao.KakaoAuthService;
import fete.be.domain.member.oauth.kakao.KakaoLoginRequest;
import fete.be.domain.member.oauth.kakao.KakaoSignUpRequest;
import fete.be.domain.member.oauth.kakao.KakaoUserInfoResponse;
import fete.be.global.jwt.JwtToken;
import fete.be.global.util.ApiResponse;
import fete.be.global.util.Logging;
import fete.be.global.util.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {
    static final boolean EXISTING_MEMBER = true;
    static final boolean NEW_MEMBER = false;

    private final MemberService memberService;
    private final KakaoAuthService kakaoAuthService;

    /**
     * 회원가입 API
     *
     * @param SignupRequestDto request
     * @return ApiResponse
     */
    @PostMapping("/signup")
    public ApiResponse signup(@RequestBody SignupRequestDto request) {
        try {
            log.info("Signup request: {}", request);
            Logging.time();

            memberService.signup(request);
            return new ApiResponse<>(ResponseMessage.SIGNUP_SUCCESS.getCode(), ResponseMessage.SIGNUP_SUCCESS.getMessage());
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.SIGNUP_DUPLICATE_EMAIL.getCode(), e.getMessage());
        }
    }


    /**
     * 로그인 API
     *
     * @param LoginRequestDto request
     * @return ApiResponse<LoginResponseDto>
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        log.info("Login request: {}", request);
        Logging.time();

        // 로그인 검증 이후, 토큰 발급
        JwtToken token = memberService.login(request.getEmail(), request.getPassword());

        // 일치하는 유저가 없을 경우
        if (token == null) {
            return new ApiResponse(ResponseMessage.LOGIN_FAILURE.getCode(), ResponseMessage.LOGIN_FAILURE.getMessage());
        }

        // 일치하는 유저가 있는 경우 - 정상 로그인 로직
        LoginResponseDto result = new LoginResponseDto(token);
        return new ApiResponse<>(ResponseMessage.LOGIN_SUCCESS.getCode(), ResponseMessage.LOGIN_SUCCESS.getMessage(), result);
    }


    /**
     * 카카오 회원가입 API
     * 1. 카카오 회원 데이터가 없는 경우 : 카카오 계정 정보의 email과 고유 id로 signUp 메서드 실행 -> kakaoLogin 재실행
     * 2. 카카오 회원 데이터가 존재하는 경우 : LoginRequestDto를 만들어 login 메서드 실행 -> jwt 토큰 발급
     *
     * @param KakaoSignUpRequest request
     * @return ApiResponse<SocialLoginResponse>
     */
    @PostMapping("/kakao/signup")
    public ApiResponse<SocialLoginResponse> kakaoSignUp(@RequestBody KakaoSignUpRequest request) {
        log.info("KakaoSignUp request: {}", request);
        Logging.time();

        // Body에서 accessToken 추출
        String accessToken = request.getAccessToken();

        try {
            // 카카오 회원인지 검사
            LoginRequestDto loginInfo = kakaoAuthService.checkSignUp(accessToken);

            // 카카오 회원인 경우 -> 로그인 실행 후 jwt 토큰 발급
            ApiResponse<LoginResponseDto> loginApiResult = login(loginInfo);
            LoginResponseDto loginResponse = loginApiResult.getResult();
            SocialLoginResponse result = new SocialLoginResponse(EXISTING_MEMBER, loginResponse.getToken());

            return new ApiResponse<>(ResponseMessage.KAKAO_LOGIN_SUCCESS.getCode(), ResponseMessage.KAKAO_LOGIN_SUCCESS.getMessage(), result);
        } catch (KakaoUserNotFoundException e) {  // 카카오 회원이 처음인 경우 -> 회원가입 실행
            // 카카오 계정 정보
            KakaoUserInfoResponse userInfo = e.getUserInfo();

            // 카카오 계정 정보, 사용자로부터 입력 받은 정보로 회원가입 DTO 생성
            SignupRequestDto signUpDto = kakaoAuthService.createSignUpDto(userInfo, request);

            // 회원가입 실행
            memberService.signup(signUpDto);

            // 카카오 로그인 재실행
            KakaoLoginRequest kakaoLoginRequest = new KakaoLoginRequest(accessToken, request.getRefreshToken());
            return kakaoLogin(kakaoLoginRequest);
        }
    }


    /**
     * 카카오 로그인 API
     * - accessToken으로 회원 여부 확인
     * - 기존 회원 -> 로그인 후, jwt 토큰 발급
     * - 신규 회원 -> 로그인 실패 결과 전달
     *
     * @param KakaoLoginRequest request
     * @return ApiResponse<SocialLoginResponse>
     */
    @PostMapping("/kakao/login")
    public ApiResponse<SocialLoginResponse> kakaoLogin(@RequestBody KakaoLoginRequest request) {
        log.info("KakaoLogin request: {}", request);
        Logging.time();

        // Body에서 accessToken 추출
        String accessToken = request.getAccessToken();

        // 카카오 회원가입 여부 확인
        try {
            // 카카오 회원인지 검사
            LoginRequestDto loginInfo = kakaoAuthService.checkSignUp(accessToken);

            // 카카오 회원인 경우 -> 로그인 실행 후 jwt 토큰 발급
            ApiResponse<LoginResponseDto> loginApiResult = login(loginInfo);
            LoginResponseDto loginResponse = loginApiResult.getResult();
            SocialLoginResponse result = new SocialLoginResponse(EXISTING_MEMBER, loginResponse.getToken());

            return new ApiResponse<>(ResponseMessage.KAKAO_LOGIN_SUCCESS.getCode(), ResponseMessage.KAKAO_LOGIN_SUCCESS.getMessage(), result);
        } catch (KakaoUserNotFoundException e) {
            // 신규 회원인 경우 -> 카카오 로그인 실패 결과 전달
            SocialLoginResponse result = new SocialLoginResponse(NEW_MEMBER, null);

            return new ApiResponse<>(ResponseMessage.KAKAO_LOGIN_FAILURE.getCode(), e.getMessage(), result);
        }
    }


    /**
     * ADMIN 요청 API
     *
     * @param GrantAdminRequestDto request
     * @return ApiResponse
     */
    @PostMapping("/admin")
    public ApiResponse grantAdmin(@RequestBody GrantAdminRequestDto request) {
        try {
            log.info("GrantAdmin request: {}", request);
            Logging.time();

            Long grantedMemberId = memberService.grantAdmin(request);
            return new ApiResponse<>(ResponseMessage.MEMBER_ADMIN_OK.getCode(), ResponseMessage.MEMBER_ADMIN_OK.getMessage());
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.MEMBER_ADMIN_REJECT.getCode(), e.getMessage());
        }
    }


    /**
     * 회원 정보 수정 API
     *
     * @param ModifyRequestDto request
     * @return ApiResponse
     */
    @PostMapping("/modify")
    public ApiResponse modify(@RequestBody ModifyRequestDto request) {
        try {
            log.info("Modify request: {}", request);
            Logging.time();

            Long modifiedMemberId = memberService.modify(request);
            return new ApiResponse<>(ResponseMessage.MEMBER_MODIFY_SUCCESS.getCode(), ResponseMessage.MEMBER_MODIFY_SUCCESS.getMessage());
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.MEMBER_MODIFY_FAILURE.getCode(), e.getMessage());
        }
    }
}