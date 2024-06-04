package fete.be.global.util;

public enum ResponseMessage {

    SUCCESS(200, "API 요청이 성공했습니다."),
    INVALID_REQUEST(201, "불가능한 요청입니다."),
    INTERNAL_ERROR(500, "내부 오류가 발생했습니다."),

    // SIGNUP
    SIGNUP_DUPLICATE_EMAIL(1000, "중복된 이메일 입니다."),

    // LOGIN
    LOGIN_SUCCESS(2000, "로그인에 성공하였습니다."),
    LOGIN_FAILURE(2001, "아이디 또는 비밀번호가 일치하지 않습니다.");

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
