package fete.be.global.util;

public enum ResponseMessage {

    SUCCESS(200, "API 요청이 성공하였습니다."),
    INVALID_REQUEST(201, "불가능한 요청입니다."),
    INTERNAL_ERROR(500, "내부 오류가 발생했습니다."),

    // MEMBER
    MEMBER_ADMIN_OK(600, "관리자 권한을 획득하였습니다."),
    MEMBER_ADMIN_REJECT(601, "관리자 권한 요청이 거부되었습니다."),
    MEMBER_ALREADY_ADMIN(602, "관리자 권한을 획득한 상태입니다."),
    MEMBER_MODIFY_SUCCESS(603, "회원 정보 변경에 성공하였습니다."),
    MEMBER_MODIFY_FAILURE(604, "회원 정보 변경에 실패하였습니다."),

    // SIGNUP
    SIGNUP_SUCCESS(1000, "성공적으로 회원가입 하였습니다."),
    SIGNUP_DUPLICATE_EMAIL(1001, "중복된 이메일 입니다."),

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

    // TICKET
    TICKET_SUCCESS(5000, "티켓 조회에 성공하였습니다."),
    TICKET_FAILURE(5001, "티켓 조회에 실패하였습니다."),
    TICKET_NO_EXIST(5002, "존재하지 않는 티켓입니다."),

    // LIKE
    LIKE_SUCCESS(6000, "관심 등록 API 요청이 성공하였습니다."),
    LIKE_FAILURE(6001, "관심 등록 API 요청이 실패하였습니다."),
    LIKE_GET_POSTER_SUCCESS(6002, "관심 등록한 포스터 조회에 성공하였습니다."),
    LIKE_GET_POSTER_FAILURE(6003, "관심 등록한 포스터 조회에 실패하였습니다."),
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
