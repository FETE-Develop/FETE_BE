package fete.be.domain.member.oauth.kakao.dto;

import fete.be.domain.member.persistence.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class KakaoSignUpRequest {
    private String accessToken;  // 카카오 응답 - accessToken
    private String refreshToken;  // 카카오 응답 - refreshToken

    @Column(nullable = false, length = 30)
    private String introduction;  // 소개글
    private String birth;  // 생년월일
    @Enumerated(EnumType.STRING)
    private Gender gender;  // 성별
    private String phoneNumber;  // 휴대전화 번호
}
