package fete.be.global.util;

public enum ResponseMessage {

    SUCCESS(200, "API 요청이 성공하였습니다."),
    INVALID_REQUEST(201, "불가능한 요청입니다."),
    INTERNAL_ERROR(500, "내부 오류가 발생했습니다."),

    // SIGNUP
    SIGNUP_DUPLICATE_EMAIL(1000, "중복된 이메일 입니다."),

    // LOGIN
    LOGIN_SUCCESS(2000, "로그인에 성공하였습니다."),
    LOGIN_FAILURE(2001, "아이디 또는 비밀번호가 일치하지 않습니다."),

    // POSTER
    POSTER_SUCCESS(3000, "포스터 API 요청이 성공하였습니다."),
    POSTER_FAILURE(3001, "포스터 API 요청이 실패하였습니다."),
    POSTER_INVALID_POSTER(3002, "해당 포스터가 존재하지 않습니다."),
    POSTER_INVALID_USER(3003, "해당 포스터의 작성자가 아닙니다."),

    // EVENT
    EVENT_QR_SUCCESS(4000, "QR 코드 발급에 성공하였습니다."),
    EVENT_QR_FAILURE(4001, "QR 코드 발급에 실패하였습니다."),
    EVENT_INVALID_QR(4002, "일치하는 정보가 존재하지 않습니다."),
    EVENT_VALID_QR(4003, "QR 코드가 인증되었습니다."),
    EVENT_INVALID_FILE(4004, "잘못된 파일입니다."),
    ;

    private int code;
    private String message;

    ResponseMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }
}
