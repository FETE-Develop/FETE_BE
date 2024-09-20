package fete.be.domain.member.application.dto.request;

import fete.be.domain.member.oauth.apple.AppleUserInfo;
import fete.be.domain.member.oauth.apple.dto.AppleSignUpRequest;
import fete.be.domain.member.oauth.kakao.dto.KakaoSignUpRequest;
import fete.be.domain.member.oauth.kakao.dto.KakaoUserInfoResponse;
import fete.be.domain.member.persistence.Gender;
import fete.be.domain.member.persistence.MemberType;
import fete.be.global.util.UUIDGenerator;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class OAuthSignupRequest {
    private MemberType memberType;
    private String email;
    private String oauthId;
    private String password;
    @Nullable
    private String profileImage;
    private String userName;
    private String introduction;
    private String birth;
    private Gender gender;
    private String phoneNumber;


    // 카카오 소셜 로그인
    public OAuthSignupRequest(KakaoUserInfoResponse userInfo, KakaoSignUpRequest request) {
        this.memberType = MemberType.KAKAO;
        this.email = userInfo.getKakao_account().getEmail();  // 카카오 계정 이메일 -> email
        this.oauthId = String.valueOf(userInfo.getId());  // 카카오 계정 고유 id
        this.password = UUIDGenerator.generatePassword(16);  // 16자리 랜덤 비밀번호 생성
        this.profileImage = userInfo.getKakao_account().getProfile().getProfile_image_url();  // 카카오 계정 프로필 이미지 -> profileImage
        this.userName = userInfo.getKakao_account().getProfile().getNickname();  // 카카오 계정 닉네임 -> userName
        this.introduction = request.getIntroduction();
        this.birth = request.getBirth();
        this.gender = request.getGender();
        this.phoneNumber = request.getPhoneNumber();
    }

    // 애플 소셜 로그인
    public OAuthSignupRequest(AppleUserInfo appleUserInfo, AppleSignUpRequest request) {
        this.memberType = MemberType.APPLE;
        this.email = appleUserInfo.getEmail();  // 애플 계정 이메일 -> email
        this.oauthId = appleUserInfo.getSub();  // 애플이 유저에게 부여하는 고유 id (sub)
        this.password = UUIDGenerator.generatePassword(16);  // 16자리 랜덤 비밀번호 생성
        this.profileImage = request.getProfileImage();
        this.userName = request.getUserName();
        this.introduction = request.getIntroduction();
        this.birth = request.getBirth();
        this.gender = request.getGender();
        this.phoneNumber = request.getPhoneNumber();
    }
}
