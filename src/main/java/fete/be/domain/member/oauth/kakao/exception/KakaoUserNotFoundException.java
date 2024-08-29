package fete.be.domain.member.oauth.kakao.exception;

import fete.be.domain.member.oauth.kakao.dto.KakaoUserInfoResponse;
import lombok.Getter;

@Getter
public class KakaoUserNotFoundException extends KakaoException{
    private final KakaoUserInfoResponse userInfo;

    public KakaoUserNotFoundException(String message, KakaoUserInfoResponse userInfo) {
        super(message);
        this.userInfo = userInfo;
    }
}
