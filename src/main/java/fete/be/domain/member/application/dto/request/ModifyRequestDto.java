package fete.be.domain.member.application.dto.request;

import fete.be.domain.member.persistence.Gender;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ModifyRequestDto {
    @Nullable
    private String profileImage;
    private String userName;
    private String introduction;
    private String birth;
    private Gender gender;
    private String phoneNumber;
}
