package fete.be.domain.member.oauth.apple.dto;

import fete.be.domain.member.persistence.Gender;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AppleSignUpRequest {
    private String idToken;  // 애플 응답 - idToken

    @Nullable
    private String profileImage;  // 프로필 이미지
    private String userName;  // 유저 이름
    @Column(nullable = false, length = 30)
    private String introduction;  // 소개글
    private String birth;  // 생년월일
    @Enumerated(EnumType.STRING)
    private Gender gender;  // 성별
    private String phoneNumber;  // 휴대전화 번호
}
