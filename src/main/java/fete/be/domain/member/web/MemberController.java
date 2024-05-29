package fete.be.domain.member.web;

import fete.be.domain.member.application.MemberService;
import fete.be.domain.member.application.dto.SignupRequestDto;
import fete.be.domain.member.persistence.Member;
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

    @PostMapping("/signup")
    public ApiResponse signup(@RequestBody SignupRequestDto request) {
        // email 중복 검사 - 조건문 필요

        // 검증에 성공할 경우
        Member member = Member.createMember(request.getEmail(), request.getPassword(), request.getUserName());
        memberService.signup(member);

        return new ApiResponse<>(ResponseMessage.SUCCESS.getCode(), ResponseMessage.SUCCESS.getMessage());
    }
}
