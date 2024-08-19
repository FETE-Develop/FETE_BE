package fete.be.domain.member.application.dto.request;

import fete.be.domain.member.oauth.kakao.KakaoLoginRequest;
import fete.be.domain.member.oauth.kakao.KakaoSignUpRequest;
import fete.be.domain.member.oauth.kakao.KakaoUserInfoResponse;
import fete.be.domain.member.persistence.Gender;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SignupRequestDto {
    private String email;
    private String password;
    private String profileImage;
    private String userName;
    private String introduction;
    private String birth;
    private Gender gender;
    private String phoneNumber;


    public SignupRequestDto(KakaoUserInfoResponse userInfo, KakaoSignUpRequest request) {
        this.email = userInfo.getKakao_account().getEmail();  // 카카오 계정 이메일 -> email
        this.password = String.valueOf(userInfo.getId());  // 카카오 계정 고유 id -> password
        this.profileImage = userInfo.getKakao_account().getProfile().getProfile_image_url();  // 카카오 계정 프로필 이미지 -> profileImage
        this.userName = userInfo.getKakao_account().getProfile().getNickname();  // 카카오 계정 닉네임 -> userName
        this.introduction = request.getIntroduction();
        this.birth = request.getBirth();
        this.gender = request.getGender();
        this.phoneNumber = request.getPhoneNumber();
    }
}
