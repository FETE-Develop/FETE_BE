package fete.be.global.util;

import java.security.SecureRandom;
import java.util.UUID;

public class UUIDGenerator {

    // 알파벳 대소문자, 숫자, 특수문자를 포함한 문자열
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_-+=<>?";
    private static final SecureRandom RANDOM = new SecureRandom();


    // 36자리 길이의 랜덤 문자열 생성
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    // n자리 길이의 랜덤 문자열 생성 (특수문자 포함)
    public static String generatePassword(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    // n자리 길이의 랜덤 숫자 문자열 생성
    public static String generateNumericString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(RANDOM.nextInt(10));  // 0부터 9까지의 숫자 랜덤 선택
        }
        return sb.toString();
    }
}
