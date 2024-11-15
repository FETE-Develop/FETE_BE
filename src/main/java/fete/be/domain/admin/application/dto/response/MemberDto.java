package fete.be.domain.admin.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import fete.be.domain.member.persistence.Gender;
import fete.be.domain.member.persistence.ProfileImage;
import fete.be.domain.member.persistence.Role;
import fete.be.global.util.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {
    private Long memberId;
    private String email;
    private String profileImage;
    private String userName;  // 닉네임
    private String introduction;  // 소개글
    private String birth;  // 생년월일(yyyy-MM-dd)
    private Gender gender;  // 성별(MALE / FEMALE)
    private String phoneNumber;  // 휴대전화 번호
    private Role role;  // 권한
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;  // 가입 날짜
    private Status status;  // 계정 상태
}
