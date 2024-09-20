package fete.be.domain.member.application.dto.response;

import fete.be.domain.member.persistence.Gender;
import fete.be.domain.member.persistence.Member;
import fete.be.domain.member.persistence.MemberType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetMyProfileResponse {
    private MemberType memberType;
    private String email;
    private String profileImage;
    private String userName;
    private String introduction;
    private String birth;
    private Gender gender;
    private String phoneNumber;

    public GetMyProfileResponse(Member member) {
        this.memberType = member.getMemberType();
        this.email = member.getEmail();
        this.profileImage = member.getProfileImage().getImageUrl();
        this.userName = member.getUserName();
        this.introduction = member.getIntroduction();
        this.birth = member.getBirth();
        this.gender = member.getGender();
        this.phoneNumber = member.getPhoneNumber();
    }
}
