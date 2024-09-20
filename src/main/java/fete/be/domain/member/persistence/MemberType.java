package fete.be.domain.member.persistence;

public enum MemberType {
    EMAIL("EMAIL"),  // 이메일
    APPLE("APPLE"),  // 애플
    KAKAO("KAKAO");  // 카카오

    private String memberType;

    MemberType(String memberType) {
        this.memberType = memberType;
    }

    public String getMemberType() {
        return this.memberType;
    }
}
