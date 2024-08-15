package fete.be.global.util;

public enum Status {
    WAIT("WAIT"),  // 승인 전
    ACTIVE("ACTIVE"),  // 승인
    DELETE("DELETE"),  // 소프트 삭제
    REPORT("REPORT"),  // 신고
    END("END")  // 종료
    ;


    private String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }
}
