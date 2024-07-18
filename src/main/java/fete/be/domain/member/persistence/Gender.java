package fete.be.domain.member.persistence;

public enum Gender {
    MALE("MALE"),
    FEMALE("FEMALE");

    private String gender;

    Gender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return this.gender;
    }
}
