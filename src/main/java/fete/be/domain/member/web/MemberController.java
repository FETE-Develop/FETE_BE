package fete.be.domain.member.web;

import fete.be.domain.member.application.MemberService;
import fete.be.domain.member.application.dto.request.GrantAdminRequestDto;
import fete.be.domain.member.application.dto.request.LoginRequestDto;
import fete.be.domain.member.application.dto.request.SignupRequestDto;
import fete.be.domain.member.application.dto.response.LoginResponseDto;
import fete.be.domain.member.persistence.Member;
import fete.be.global.jwt.JwtToken;
import fete.be.global.util.ApiResponse;
import fete.be.global.util.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원가입 API
     *
     * @param SignupRequestDto request
     * @return ApiResponse
     */
    @PostMapping("/signup")
    public ApiResponse signup(@RequestBody SignupRequestDto request) {
        try {
            memberService.signup(request);
            return new ApiResponse<>(ResponseMessage.SIGNUP_SUCCESS.getCode(), ResponseMessage.SIGNUP_SUCCESS.getMessage());
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.SIGNUP_DUPLICATE_EMAIL.getCode(), ResponseMessage.SIGNUP_DUPLICATE_EMAIL.getMessage());
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
     * ADMIN 요청 API
     *
     * @param GrantAdminRequestDto request
     * @return ApiResponse
     */
    @PostMapping("/admin")
    public ApiResponse grantAdmin(@RequestBody GrantAdminRequestDto request) {
        try {
            Long grantedMemberId = memberService.grantAdmin(request);
            return new ApiResponse<>(ResponseMessage.MEMBER_ADMIN_OK.getCode(), ResponseMessage.MEMBER_ADMIN_OK.getMessage());
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(ResponseMessage.MEMBER_ADMIN_REJECT.getCode(), e.getMessage());
        }
    }
}
