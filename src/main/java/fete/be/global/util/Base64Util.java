package fete.be.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class Base64Util {

    private static
    ObjectMapper objectMapper = new ObjectMapper();

    // Base64 인코딩 함수
    public static String encode(String data) {
        return Base64.getEncoder().encodeToString(data.getBytes());
    }

    // Base64 디코딩 함수
    public static String decode(String base64Data) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64Data);
        return new String(decodedBytes);
    }

    // Base64 디코딩 함수
    public static byte[] decodeBytes(String base64Data) {
        return Base64.getDecoder().decode(base64Data);
    }

    // JSON 파일을 문자열로 변환하는 함수
    public static String convertJsonFileToString(String filePath) throws IOException {
        // JSON 파일을 읽어 문자열로 변환
        return objectMapper.writeValueAsString(objectMapper.readTree(new File(filePath)));
    }
}
