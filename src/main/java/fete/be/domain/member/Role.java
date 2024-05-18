package fete.be.domain.member;

public enum Role {
    USER("ROLE_USER"),  // 일반 유저
    JOIN_USER("ROLE_USER,ROLE_JOIN"),  // 가입한 유저
    ADMIN("ROLE_USER,ROLE_ADMIN");  // 관리자

    private String roles;

    Role(String roles) {
        this.roles = roles;
    }

    public String getRoles() {
        return this.roles;
    }
}
