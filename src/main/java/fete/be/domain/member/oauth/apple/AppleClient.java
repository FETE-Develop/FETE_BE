package fete.be.domain.member.oauth.apple;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "apple-public-key", url = "https://appleid.apple.com")
public interface AppleClient {

    /**
     * 애플의 공개 키들을 가져오는 API
     */
    @GetMapping("/auth/keys")
    ApplePublicKeys getApplePublicKeys();
}
