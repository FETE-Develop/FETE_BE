package fete.be.domain.member.application.dto.request;

import fete.be.domain.member.persistence.Gender;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ModifyRequestDto {
    @Nullable
    private String profileImage;
    private String userName;
    private String introduction;
    private String birth;
    private Gender gender;
    private String phoneNumber;
}
