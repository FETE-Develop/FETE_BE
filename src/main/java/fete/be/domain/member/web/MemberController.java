package fete.be.domain.member.web;

import fete.be.domain.member.application.MemberService;
import fete.be.domain.member.application.dto.request.*;
import fete.be.domain.member.application.dto.response.*;
import fete.be.domain.member.exception.*;
import fete.be.domain.member.oauth.apple.exception.AppleUserNotFoundException;
import fete.be.domain.member.oauth.kakao.exception.KakaoUserNotFoundException;
import fete.be.domain.member.oauth.apple.dto.AppleLoginRequest;
import fete.be.domain.member.oauth.apple.AppleAuthService;
import fete.be.domain.member.oauth.apple.dto.AppleSignUpRequest;
import fete.be.domain.member.oauth.apple.AppleUserInfo;
import fete.be.domain.member.oauth.kakao.KakaoAuthService;
import fete.be.domain.member.oauth.kakao.dto.KakaoLoginRequest;
import fete.be.domain.member.oauth.kakao.dto.KakaoSignUpRequest;
import fete.be.domain.member.oauth.kakao.dto.KakaoUserInfoResponse;
import fete.be.global.jwt.JwtProvider;
import fete.be.global.jwt.JwtToken;
import fete.be.global.util.ApiResponse;
import fete.be.global.util.Logging;
import fete.be.global.util.ResponseMessage;
import jakarta.validation.Valid;
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
    private final AppleAuthService appleAuthService;
    private final JwtProvider jwtProvider;


    /**
     * 회원가입 API
     *
     * @param SignupRequestDto request
     * @return ApiResponse
     */
    @PostMapping("/signup")
    public ApiResponse signup(@RequestBody SignupRequestDto request) {
        try {
            // 회원가입 실행
            memberService.signUp(request);
            return new ApiResponse<>(ResponseMessage.SIGNUP_SUCCESS.getCode(), ResponseMessage.SIGNUP_SUCCESS.getMessage());
        } catch (DuplicateEmailException e) {
            return new ApiResponse<>(ResponseMessage.SIGNUP_DUPLICATE_EMAIL.getCode(), e.getMessage());
        } catch (DuplicatePhoneNumberException e) {
            return new ApiResponse<>(ResponseMessage.SIGNUP_DUPLICATE_PHONE_NUMBER.getCode(), e.getMessage());
        } catch (BlockedUserException e) {
            return new ApiResponse<>(ResponseMessage.MEMBER_BLOCKED.getCode(), e.getMessage());
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
            OAuthSignupRequest signUpDto = kakaoAuthService.createSignUpDto(userInfo, request);

            // 회원가입 실행
            memberService.oauthSignUp(signUpDto);

            // 카카오 로그인 재실행
            KakaoLoginRequest kakaoLoginRequest = new KakaoLoginRequest(accessToken, request.getRefreshToken());
            return kakaoLogin(kakaoLoginRequest);
        } catch (DuplicateEmailException e) {
            return new ApiResponse<>(ResponseMessage.SIGNUP_DUPLICATE_EMAIL.getCode(), e.getMessage());
        } catch (DuplicatePhoneNumberException e) {
            return new ApiResponse<>(ResponseMessage.SIGNUP_DUPLICATE_PHONE_NUMBER.getCode(), e.getMessage());
        } catch (BlockedUserException e) {
            return new ApiResponse<>(ResponseMessage.MEMBER_BLOCKED.getCode(), e.getMessage());
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
     * 애플 회원가입 API
     * 1. 애플 회원 데이터가 없는 경우 : 애플 계정 정보의 email과 sub로 signUp 메서드 실행 -> appleLogin 재실행
     * 2. 애플 회원 데이터가 존재하는 경우 : LoginRequestDto를 만들어 login 메서드 실행 -> jwt 토큰 발급
     *
     * @param AppleSignUpRequest request
     * @return ApiResponse<SocialLoginResponse>
     */
    @PostMapping("/apple/signup")
    public ApiResponse<SocialLoginResponse> appleSignUp(@RequestBody AppleSignUpRequest request) {
        // 애플의 idToken 추출
        String idToken = request.getIdToken();

        try {
            // 기존 회원인지 검사
            LoginRequestDto loginInfo = appleAuthService.checkSignUp(idToken);

            // 기존 회원인 경우 -> 로그인 실행 후 jwt 토큰 발급
            ApiResponse<LoginResponseDto> loginApiResult = login(loginInfo);
            LoginResponseDto loginResponse = loginApiResult.getResult();
            SocialLoginResponse result = new SocialLoginResponse(EXISTING_MEMBER, loginResponse.getToken());

            return new ApiResponse<>(ResponseMessage.APPLE_LOGIN_SUCCESS.getCode(), ResponseMessage.APPLE_LOGIN_SUCCESS.getMessage(), result);
        } catch (AppleUserNotFoundException e) {  // 애플 회원이 처음인 경우 -> 회원가입 실행
            // 애플 계정 정보
            AppleUserInfo appleUserInfo = e.getAppleUserInfo();

            // 애플 계정 정보, 사용자로부터 입력 받은 정보로 회원가입 DTO 생성
            OAuthSignupRequest signUpDto = appleAuthService.createSignUpDto(appleUserInfo, request);

            // 회원가입 실행
            memberService.oauthSignUp(signUpDto);

            // 애플 로그인 재실행
            AppleLoginRequest appleLoginRequest = new AppleLoginRequest(idToken);
            return appleLogin(appleLoginRequest);
        } catch (DuplicateEmailException e) {
            return new ApiResponse<>(ResponseMessage.SIGNUP_DUPLICATE_EMAIL.getCode(), e.getMessage());
        } catch (DuplicatePhoneNumberException e) {
            return new ApiResponse<>(ResponseMessage.SIGNUP_DUPLICATE_PHONE_NUMBER.getCode(), e.getMessage());
        } catch (BlockedUserException e) {
            return new ApiResponse<>(ResponseMessage.MEMBER_BLOCKED.getCode(), e.getMessage());
        }
    }


    /**
     * 애플 로그인 API
     * - idToken으로 회원 여부 확인
     * - 기존 회원 -> 로그인 후, jwt 토큰 발급
     * - 신규 회원 -> 로그인 실패 결과 전달
     *
     * @param AppleLoginRequest request
     * @return ApiResponse<SocialLoginResponse>
     */
    @PostMapping("/apple/login")
    public ApiResponse<SocialLoginResponse> appleLogin(@RequestBody AppleLoginRequest request) {
        // 애플의 idToken 추출
        String idToken = request.getIdToken();

        try {
            // idToken 토큰을 통해 애플 회원 여부 확인
            LoginRequestDto loginInfo = appleAuthService.checkSignUp(idToken);

            // 기존 회원인 경우 -> 로그인 실행 후 jwt 토큰 발급
            ApiResponse<LoginResponseDto> loginApiResult = login(loginInfo);
            LoginResponseDto loginResponse = loginApiResult.getResult();
            SocialLoginResponse result = new SocialLoginResponse(EXISTING_MEMBER, loginResponse.getToken());

            return new ApiResponse<>(ResponseMessage.APPLE_LOGIN_SUCCESS.getCode(), ResponseMessage.APPLE_LOGIN_SUCCESS.getMessage(), result);
        } catch (AppleUserNotFoundException e) {
            // 신규 회원인 경우 -> 애플 로그인 실패 결과 전달
            SocialLoginResponse result = new SocialLoginResponse(NEW_MEMBER, null);

            return new ApiResponse<>(ResponseMessage.APPLE_LOGIN_FAILURE.getCode(), e.getMessage(), result);
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
            // admin 권한 승인
            Long grantedMemberId = memberService.grantAdmin(request);
            return new ApiResponse<>(ResponseMessage.MEMBER_ADMIN_OK.getCode(), ResponseMessage.MEMBER_ADMIN_OK.getMessage());
        } catch (GuestUserException e) {
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
            // 회원 정보 수정
            Long modifiedMemberId = memberService.modify(request);
            return new ApiResponse<>(ResponseMessage.MEMBER_MODIFY_SUCCESS.getCode(), ResponseMessage.MEMBER_MODIFY_SUCCESS.getMessage());
        } catch (GuestUserException e) {
            return new ApiResponse<>(ResponseMessage.MEMBER_MODIFY_FAILURE.getCode(), e.getMessage());
        }
    }


    /**
     * 회원 프로필 조회 API
     */
    @GetMapping("/my-profile")
    public ApiResponse<GetMyProfileResponse> getMyProfile() {
        try {
            // 프로필 조회
            GetMyProfileResponse result = memberService.getMyProfile();
            return new ApiResponse<>(ResponseMessage.MEMBER_GET_PROFILE_SUCCESS.getCode(), ResponseMessage.MEMBER_GET_PROFILE_SUCCESS.getMessage(), result);
        } catch (GuestUserException e) {
            return new ApiResponse<>(ResponseMessage.MEMBER_GET_PROFILE_FAIL.getCode(), e.getMessage());
        }
    }


    /**
     * 회원 탈퇴 API
     */
    @PostMapping("/deactivate")
    public ApiResponse deactivateMember() {
        try {
            // 회원 삭제
            memberService.deactivateMember();
            return new ApiResponse<>(ResponseMessage.MEMBER_DEACTIVATE_SUCCESS.getCode(), ResponseMessage.MEMBER_DEACTIVATE_SUCCESS.getMessage());
        } catch (GuestUserException e) {
            return new ApiResponse<>(ResponseMessage.MEMBER_DEACTIVATE_FAIL.getCode(), e.getMessage());
        }
    }


    /**
     * 아이디 찾기 API
     */
    @PostMapping("/find-id")
    public ApiResponse<FindIdResponse> findId(@RequestBody FindIdRequest request) {
        // 휴대전화 번호 추출
        String phoneNumber = request.getPhoneNumber();

        try {
            // 휴대전화 번호로 아이디 조회
            FindIdResponse result = memberService.findId(phoneNumber);

            return new ApiResponse<>(ResponseMessage.MEMBER_FIND_ID_SUCCESS.getCode(), ResponseMessage.MEMBER_FIND_ID_SUCCESS.getMessage(), result);
        } catch (NotFoundMemberException e) {
            return new ApiResponse<>(ResponseMessage.MEMBER_FIND_ID_FAIL.getCode(), e.getMessage());
        }
    }


    /**
     * 비밀번호 찾기 API
     */
    @PostMapping("/find-password")
    public ApiResponse<FindPasswordResponse> findPassword(@RequestBody FindPasswordRequest request) {
        // 이메일 추출
        String email = request.getEmail();

        try {
            // 이메일로 회원을 조회하여 임시 비밀번호 발급
            FindPasswordResponse result = memberService.findPassword(email);

            return new ApiResponse<>(ResponseMessage.MEMBER_FIND_PW_SUCCESS.getCode(), ResponseMessage.MEMBER_FIND_PW_SUCCESS.getMessage(), result);
        } catch (NotFoundMemberException e) {
            return new ApiResponse<>(ResponseMessage.MEMBER_FIND_PW_FAIL.getCode(), e.getMessage());
        }
    }


    /**
     * 비밀번호 변경 API
     */
    @PostMapping("/modify-password")
    public ApiResponse modifyPassword(@Valid @RequestBody ModifyPasswordRequest request) {
        // 변경할 비밀번호 추출
        String password = request.getPassword();

        try {
            // 이메일로 회원을 조회하여 임시 비밀번호 발급
            memberService.modifyPassword(password);

            return new ApiResponse<>(ResponseMessage.MEMBER_MODIFY_PW_SUCCESS.getCode(), ResponseMessage.MEMBER_MODIFY_PW_SUCCESS.getMessage());
        } catch (GuestUserException e) {
            return new ApiResponse<>(ResponseMessage.MEMBER_MODIFY_PW_FAIL.getCode(), e.getMessage());
        }
    }


    /**
     * 토큰 유효성 검사 API
     *
     * @param CheckJwtTokenRequest request
     * @return ApiResponse<Boolean>
     */
    @PostMapping("/check-token")
    public ApiResponse<Boolean> checkJwtToken(@RequestBody CheckJwtTokenRequest request) {
        // 확인할 토큰 추출
        String token = request.getToken();

        // 토큰 검사
        Boolean isValidToken = jwtProvider.validateToken(token);
        return new ApiResponse<>(ResponseMessage.MEMBER_CHECK_TOKEN.getCode(), ResponseMessage.MEMBER_CHECK_TOKEN.getMessage(), isValidToken);
    }


    /**
     * refreshToken을 통해 accessToken을 발급해주는 API
     *
     * @param GenerateAccessTokenRequest request
     * @return ApiResponse<String>
     */
    @PostMapping("/check-refresh")
    public ApiResponse<String> generateAccessToken(@RequestBody GenerateAccessTokenRequest request) {
        // refreshToken 추출
        String refreshToken = request.getRefreshToken();

        // 토큰의 유효성 확인 후, 새로운 accessToken 발급
        String accessToken = memberService.generateAccessToken(refreshToken);

        return new ApiResponse<>(ResponseMessage.TOKEN_GENERATE_SUCCESS.getCode(), ResponseMessage.TOKEN_GENERATE_SUCCESS.getMessage(), accessToken);
    }
}