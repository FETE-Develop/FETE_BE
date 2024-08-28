package fete.be.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisUtil {

    private final StringRedisTemplate redisTemplate;

    /**
     * 데이터 저장
     * - key : value 형식
     * - expiredTime 설정 가능 (단위 : Seconds)
     */
    public void setData(String key, String value, long expiredTime) {
        redisTemplate.opsForValue().set(key, value, expiredTime, TimeUnit.SECONDS);
    }

    // 데이터 조회
    public String getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // 데이터 삭제
    public void deleteData(String key) {
        redisTemplate.delete(key);
    }

    // 데이터가 존재하는지 확인
    public boolean hasData(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
