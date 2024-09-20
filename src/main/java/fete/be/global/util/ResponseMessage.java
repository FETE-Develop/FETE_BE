package fete.be.global.util;

public enum ResponseMessage {

    SUCCESS(200, "API 요청이 성공하였습니다."),
    INVALID_REQUEST(201, "불가능한 요청입니다."),

    // GLOBAL
    INTERNAL_ERROR(500, "내부 오류가 발생했습니다."),
    NO_RESOURCE_ERROR(501, "잘못된 URI 입니다."),

    // MEMBER
    MEMBER_ADMIN_OK(600, "관리자 권한을 획득하였습니다."),
    MEMBER_ADMIN_REJECT(601, "관리자 권한 요청이 거부되었습니다."),
    MEMBER_ALREADY_ADMIN(602, "관리자 권한을 획득한 상태입니다."),
    MEMBER_MODIFY_SUCCESS(603, "회원 정보 변경에 성공하였습니다."),
    MEMBER_MODIFY_FAILURE(604, "회원 정보 변경에 실패하였습니다."),
    MEMBER_NO_EXIST(605, "해당 회원이 존재하지 않습니다."),
    MEMBER_BLOCKED(606, "차단된 회원입니다."),
    MEMBER_GET_PROFILE_SUCCESS(607, "프로필 조회에 성공하였습니다."),
    MEMBER_GET_PROFILE_FAIL(608, "프로필 조회에 실패하였습니다."),
    MEMBER_DEACTIVATE_SUCCESS(609, "회원 탈퇴에 성공하였습니다."),
    MEMBER_DEACTIVATE_FAIL(610, "회원 탈퇴에 성공하였습니다."),
    MEMBER_FIND_ID_SUCCESS(611, "아이디 조회에 성공하였습니다."),
    MEMBER_FIND_ID_FAIL(612, "아이디 조회에 실패하였습니다."),
    MEMBER_FIND_PW_SUCCESS(613, "비밀번호 조회에 성공하였습니다."),
    MEMBER_FIND_PW_FAIL(614, "비밀번호 조회에 실패하였습니다."),
    MEMBER_NOT_FOUND(615, "해당 정보로 등록된 회원이 존재하지 않습니다."),

    // EMAIL
    EMAIL_SEND_SUCCESS(700, "이메일 전송이 성공하였습니다."),
    EMAIL_SEND_FAIL(701, "이메일 전송이 실패하였습니다."),
    EMAIL_CORRECT_CODE(702, "인증번호가 일치합니다."),
    EMAIL_INCORRECT_CODE(703, "인증번호가 일치하지 않습니다."),


    // SIGNUP
    SIGNUP_SUCCESS(800, "성공적으로 회원가입 하였습니다."),
    SIGNUP_DUPLICATE_EMAIL(801, "중복된 이메일 입니다."),

    // LOGIN
    LOGIN_SUCCESS(900, "로그인에 성공하였습니다."),
    LOGIN_FAILURE(901, "아이디 또는 비밀번호가 일치하지 않습니다."),
    KAKAO_LOGIN_SUCCESS(902, "카카오 로그인에 성공하였습니다."),
    KAKAO_LOGIN_FAILURE(903, "카카오 로그인에 실패하였습니다."),
    APPLE_LOGIN_SUCCESS(904, "애플 로그인에 성공하였습니다."),
    APPLE_LOGIN_FAILURE(905, "애플 로그인에 실패하였습니다."),


    // POSTER
    POSTER_SUCCESS(1000, "포스터 API 요청이 성공하였습니다."),
    POSTER_FAILURE(1001, "포스터 API 요청이 실패하였습니다."),
    POSTER_NO_EXIST(1002, "해당 포스터가 존재하지 않습니다."),
    POSTER_INVALID_USER(1003, "해당 포스터의 작성자가 아닙니다."),
    POSTER_INVALID_EVENT(1004, "해당 이벤트가 존재하지 않습니다."),
    POSTER_SEARCH_SUCCESS(1005, "포스터 검색에 성공하였습니다."),
    POSTER_SEARCH_FAILURE(1006, "포스터 검색에 실패하였습니다."),

    // EVENT
    EVENT_QR_SUCCESS(1100, "QR 코드 발급에 성공하였습니다."),
    EVENT_QR_FAILURE(1101, "QR 코드 발급에 실패하였습니다."),
    EVENT_INVALID_QR(1102, "일치하는 정보가 존재하지 않습니다."),
    EVENT_VALID_QR(1103, "QR 코드가 인증되었습니다."),
    EVENT_INVALID_FILE(1104, "잘못된 파일입니다."),
    EVENT_QR_ALREADY_USED(1105, "이미 사용된 QR 코드입니다."),
    EVENT_NO_EXIST(1106, "해당 이벤트가 존재하지 않습니다."),
    EVENT_INCORRECT_MANAGER(1107, "해당 이벤트의 담당자가 아닙니다."),
    EVENT_INVALID_PLACE(1108, "올바르지 않은 이벤트 장소입니다."),

    // TICKET
    TICKET_SUCCESS(1200, "티켓 조회에 성공하였습니다."),
    TICKET_FAILURE(1201, "티켓 조회에 실패하였습니다."),
    TICKET_NO_EXIST(1202, "존재하지 않는 티켓입니다."),
    TICKET_CANCEL_SUCCESS(1203, "티켓 취소가 성공하였습니다."),
    TICKET_CANCEL_FAILURE(1204, "티켓 취소가 실패하였습니다."),
    TICKET_GET_CUSTOMER_KEY_SUCCESS(1205, "고객 키 조회에 성공하였습니다."),
    TICKET_GET_CUSTOMER_KEY_FAILURE(1206, "고객 키 조회에 실패하였습니다."),
    TICKET_IS_NOT_PAID(1207, "결제되지 않은 티켓입니다."),
    TICKET_INVALID_AMOUNT(1208, "티켓의 가격이 올바르지 않습니다."),
    TICKET_INVALID_CANCEL_REASON(1209, "취소 사유가 올바르지 않습니다."),
    TICKET_ENOUGH_QUANTITY(1210, "티켓의 수량이 충분합니다."),
    TICKET_NOT_ENOUGH_QUANTITY(1212, "티켓의 수량이 부족합니다."),
    TICKET_INVALID_TYPE(1213, "티켓의 종류가 올바르지 않습니다."),

    // LIKE
    LIKE_SUCCESS(1300, "관심 등록 API 요청이 성공하였습니다."),
    LIKE_FAILURE(1301, "관심 등록 API 요청이 실패하였습니다."),
    LIKE_GET_POSTER_SUCCESS(1302, "관심 등록한 포스터 조회에 성공하였습니다."),
    LIKE_GET_POSTER_FAILURE(1303, "관심 등록한 포스터 조회에 실패하였습니다."),

    // ADMIN
    ADMIN_APPROVE_POSTERS(7000, "관리자의 포스터 승인이 성공하였습니다."),
    ADMIN_APPROVE_POSTERS_FAIL(7001, "관리자의 포스터 승인이 실패하였습니다."),
    ADMIN_GET_MEMBERS(7002, "관리자의 유저 정보 리스트 조회에 성공하였습니다."),
    ADMIN_GET_MEMBERS_FAIL(7003, "관리자의 유저 정보 리스트 조회에 실패하였습니다."),
    ADMIN_GET_PAYMENTS(7004, "관리자의 이벤트 결제 정보 조회에 성공하였습니다."),
    ADMIN_GET_PAYMENTS_FAIL(7005, "관리자의 이벤트 결제 정보 조회에 실패하였습니다."),
    ADMIN_CREATE_BANNER(7006, "관리자의 배너 생성이 성공하였습니다."),
    ADMIN_CREATE_BANNER_FAIL(7007, "관리자의 배너 생성이 실패하였습니다."),
    ADMIN_MODIFY_BANNER(7008, "관리자의 배너 수정이 성공하였습니다."),
    ADMIN_MODIFY_BANNER_FAIL(7009, "관리자의 배너 수정이 실패하였습니다."),
    ADMIN_DELETE_BANNER(7010, "관리자의 배너 삭제가 성공하였습니다."),
    ADMIN_DELETE_BANNER_FAIL(7011, "관리자의 배너 삭제가 실패하였습니다."),
    ADMIN_GET_POSTERS(7012, "관리자의 포스터 간편 전체 조회에 성공하였습니다."),
    ADMIN_GET_POSTERS_FAIL(7013, "관리자의 포스터 간편 전체 조회에 실패하였습니다."),
    ADMIN_CREATE_POPUP_SUCCESS(7014, "팝업 생성이 성공하였습니다."),
    ADMIN_CREATE_POPUP_FAIL(7015, "팝업 생성이 실패하였습니다."),
    ADMIN_MODIFY_POPUP_SUCCESS(7016, "팝업 수정이 성공하였습니다."),
    ADMIN_MODIFY_POPUP_FAIL(7017, "팝업 수정이 실패하였습니다."),
    ADMIN_DELETE_POPUP_SUCCESS(7018, "팝업 삭제가 성공하였습니다."),
    ADMIN_DELETE_POPUP_FAIL(7019, "팝업 삭제가 실패하였습니다."),
    ADMIN_DEACTIVATE_MEMBER_SUCCESS(7020, "유저 강제 탈퇴가 성공하였습니다."),
    ADMIN_DEACTIVATE_MEMBER_FAIL(7021, "유저 강제 탈퇴가 실패하였습니다."),
    ADMIN_CREATE_CATEGORY_SUCCESS(7022, "카테고리 생성이 성공하였습니다."),
    ADMIN_CREATE_CATEGORY_FAIL(7023, "카테고리 생성이 성공하였습니다."),
    ADMIN_MODIFY_CATEGORY_SUCCESS(7024, "카테고리 수정이 성공하였습니다."),
    ADMIN_MODIFY_CATEGORY_FAIL(7025, "카테고리 수정이 실패하였습니다."),
    ADMIN_DELETE_CATEGORY_SUCCESS(7026, "카테고리 삭제가 성공하였습니다."),
    ADMIN_DELETE_CATEGORY_FAIL(7027, "카테고리 삭제가 실패하였습니다."),
    ADMIN_ALL_NOTIFICATION_SUCCESS(7028, "전체 푸시 알림 전송이 성공하였습니다."),
    ADMIN_NOTIFICATION_FAILURE(7029, "푸시 알림 전송이 실패하였습니다."),
    ADMIN_REGISTER_ARTIST_PROFILE_SUCCESS(7030, "아티스트 프로필 이미지 등록이 성공하였습니다."),
    ADMIN_REGISTER_ARTIST_PROFILE_FAIL(7031, "아티스트 프로필 이미지 등록이 실패하였습니다."),
    ADMIN_INVALID_ARTIST_PROFILE_COUNT(7032, "아티스트 프로필 이미지 개수가 일치하지 않습니다."),
    ADMIN_CREATE_NOTICE_SUCCESS(7033, "공지사항 생성이 성공하였습니다."),
    ADMIN_CREATE_NOTICE_FAIL(7034, "공지사항 생성이 실패하였습니다."),
    ADMIN_MODIFY_NOTICE_SUCCESS(7035, "공지사항 수정이 성공하였습니다."),
    ADMIN_MODIFY_NOTICE_FAIL(7036, "공지사항 수정이 실패하였습니다."),
    ADMIN_DELETE_NOTICE_SUCCESS(7037, "공지사항 삭제가 성공하였습니다."),
    ADMIN_DELETE_NOTICE_FAIL(7038, "공지사항 삭제가 실패하였습니다."),

    // BANNER
    BANNER_GET_BANNERS(1400, "배너 전체 조회에 성공하였습니다."),
    BANNER_GET_BANNERS_FAIL(1401, "배너 전체 조회에 실패하였습니다."),
    BANNER_NO_EXIST(1402, "해당 배너가 존재하지 않습니다."),

    // POPUP
    POPUP_GET_POPUPS(1500, "팝업 전체 조회에 성공하였습니다."),
    POPUP_GET_POPUPS_FAIL(1501, "팝업 전체 조회에 실패하였습니다."),

    // CATEGORY
    CATEGORY_GET_CATEGORIES(1600, "카테고리가 전체 조회에 성공하였습니다."),
    CATEGORY_GET_CATEGORIES_FAIL(1601, "카테고리가 전체 조회에 실패하였습니다."),
    CATEGORY_NO_EXIST(1602, "해당 카테고리가 존재하지 않습니다."),

    // NOTIFICATION
    NOTIFICATION_STORE_FCM_TOKEN(1700, "유저의 FCM 키 저장이 성공하였습니다."),
    NOTIFICATION_STORE_FCM_TOKEN_FAILURE(1701, "유저의 FCM 키 저장이 실패하였습니다."),

    // NOTICE
    NOTICE_NO_EXIST(1800, "해당 공지사항이 존재하지 않습니다."),
    NOTICE_GET_SIMPLE_NOTICES(1801, "공지사항 전체 조회에 성공하였습니다."),
    NOTICE_GET_SIMPLE_NOTICES_FAIL(1802, "공지사항 전체 조회에 실패하였습니다."),
    NOTICE_GET_NOTICE(1803, "공지사항 단건 조회에 성공하였습니다."),
    NOTICE_GET_NOTICE_FAIL(1804, "공지사항 단건 조회에 실패하였습니다."),

    // IMAGE
    S3_UPLOAD_SUCCESS(1900, "이미지 업로드가 성공하였습니다."),
    S3_UPLOAD_FAIL(1901, "이미지 업로드가 실패하였습니다."),
    AWS_SDK_ERROR(1902, "AWS SDK에 오류가 발생했습니다."),
    IMAGE_DUPLICATE(1903, "중복 이미지가 존재합니다."),
    INVALID_EXTENSION(1904, "이미지의 확장자가 올바르지 않습니다."),
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
