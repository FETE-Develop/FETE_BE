package fete.be.domain.member.persistence;

public enum Role {
    USER("ROLE_USER"),  // 일반 유저
    JOIN("ROLE_JOIN"),  // 가입한 유저
    ADMIN("ROLE_ADMIN");  // 관리자

    private String roles;

    Role(String roles) {
        this.roles = roles;
    }

    public String getRoles() {
        return this.roles;
    }
}
