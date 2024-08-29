package fete.be.domain.member.oauth.apple;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class AppleUserInfo {
    private String email;
    private String sub;
}
