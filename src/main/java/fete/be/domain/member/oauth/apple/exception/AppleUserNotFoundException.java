package fete.be.domain.member.oauth.apple.exception;

import fete.be.domain.member.oauth.apple.AppleUserInfo;
import lombok.Getter;

@Getter
public class AppleUserNotFoundException extends AppleException{
    private final AppleUserInfo appleUserInfo;

    public AppleUserNotFoundException(String message, AppleUserInfo appleUserInfo) {
        super(message);
        this.appleUserInfo = appleUserInfo;
    }
}
