package fete.be.domain.member.application.dto.request;

import fete.be.domain.member.persistence.Gender;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ModifyRequestDto {
    private String password;
    private String profileImage;
    private String userName;
    private String introduction;
    private String birth;
    private Gender gender;
    private String phoneNumber;
}
