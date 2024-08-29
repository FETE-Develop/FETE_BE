package fete.be.domain.member.oauth.kakao.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoUserInfoResponse {
    private Long id;  // 회원번호
    private String connected_at;  // 서비스 연결 시각
//    private KakaoProperties properties;  // 사용자 프로퍼티
    private KakaoAccount kakao_account;  // 카카오계정 정보

//    @Getter
//    public class KakaoProperties {
//        private String nickname;  // 닉네임
//        private
//    }

    @Getter
    public static class KakaoAccount {
        private Boolean profile_needs_agreement;  // 프로필 정보 사용자 동의 정보
        private KakaoProfile profile;  // 프로필 정보(닉네임, 프로필 사진)
        private Boolean email_needs_agreement;  // 카카오계정 이메일 사용자 동의 정보
        private String email;  // 카카오계정 이메일
    }

    @Getter
    public static class KakaoProfile {
        private String nickname;
        private String thumbnail_image_url;
        private String profile_image_url;
        private Boolean is_default_image;
        private Boolean is_default_nickname;
    }
}
