package fete.be.domain.member.web;

import fete.be.domain.member.application.MemberService;
import fete.be.domain.member.application.dto.request.LoginRequestDto;
import fete.be.domain.member.application.dto.request.SignupRequestDto;
import fete.be.domain.member.persistence.Member;
import fete.be.global.jwt.JwtToken;
import fete.be.global.util.ApiResponse;
import fete.be.global.util.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원가입 API
     * @param SignupRequestDto request
     * @return ApiResponse
     */
    @PostMapping("/signup")
    public ApiResponse signup(@RequestBody SignupRequestDto request) {
        // email 중복 검사
        if (memberService.isDuplicateEmail(request.getEmail())) {
            throw new IllegalArgumentException(ResponseMessage.SIGNUP_DUPLICATE_EMAIL.getMessage());
        }

        // 검증에 성공할 경우
        Member member = Member.createMember(request.getEmail(), request.getPassword(), request.getUserName());
        memberService.signup(member);

        return new ApiResponse<>(ResponseMessage.SUCCESS.getCode(), ResponseMessage.SUCCESS.getMessage());
    }

    /**
     * 로그인 API
     * @param LoginRequestDto request
     * @return ApiResponse
     */
    @PostMapping("/login")
    public ApiResponse login(@RequestBody LoginRequestDto request) {
        // 로그인 검증 이후, 토큰 발급
        JwtToken token = memberService.login(request.getEmail(), request.getPassword());

        // 일치하는 유저가 없을 경우
        if (token == null) {
            return new ApiResponse(ResponseMessage.LOGIN_FAILURE.getCode(), ResponseMessage.LOGIN_FAILURE.getMessage());
        }

        // 일치하는 유저가 있는 경우 - 로그인 로직
        return new ApiResponse(ResponseMessage.LOGIN_SUCCESS.getCode(), ResponseMessage.LOGIN_SUCCESS.getMessage());
    }
}
