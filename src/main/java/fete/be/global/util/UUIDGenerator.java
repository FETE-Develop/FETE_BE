package fete.be.global.util;

import java.util.UUID;

public class UUIDGenerator {
    // 36자리 길이의 랜덤 문자열 생성
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }
}
