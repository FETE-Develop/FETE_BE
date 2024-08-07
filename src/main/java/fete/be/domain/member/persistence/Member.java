package fete.be.domain.member.persistence;

import fete.be.domain.member.application.dto.request.ModifyRequestDto;
import fete.be.domain.member.application.dto.request.SignupRequestDto;
import fete.be.global.util.Status;
import fete.be.domain.ticket.persistence.Participant;
import fete.be.domain.payment.persistence.Payment;
import fete.be.domain.poster.persistence.Poster;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    private String password;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private ProfileImage profileImage;  // 프로필 이미지

    @NotBlank
    @Column(nullable = false, length = 20)
    private String userName;  // 닉네임

    @Column(nullable = false, length = 30)
    private String introduction;  // 소개글

    @NotBlank
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "생년월일 형식은 yyyy-MM-dd 입니다.")
    private String birth;  // 생년월일(yyyy-MM-dd)

    @NotNull
    @Enumerated(EnumType.STRING)
    private Gender gender;  // 성별(MALE / FEMALE)

    @NotBlank
    @Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 10~11 자리의 숫자만 입력 가능합니다.")
    private String phoneNumber;  // 휴대전화 번호

    @Enumerated(EnumType.STRING)
    private Role role;  // 권한

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Participant> participants = new ArrayList<>();  // 유저가 참여한 이벤트 리스트

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Payment> payments = new ArrayList<>();  // 유저의 결제 정보 리스트

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Poster> posters = new ArrayList<>();  // 유저가 등록한 프스터 리스트

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private Status status;


    // 생성 메서드
    public static Member createMember(SignupRequestDto request) {
        Member member = new Member();
        member.email = request.getEmail();
        member.password = request.getPassword();

        // 프로필 이미지 생성
        member.profileImage = ProfileImage.createProfileImage(member, request.getProfileImage());

        member.userName = request.getUserName();
        member.introduction = request.getIntroduction();
        member.birth = request.getBirth();
        member.gender = request.getGender();
        member.phoneNumber = request.getPhoneNumber();
        member.role = Role.USER;

        LocalDateTime currentTime = LocalDateTime.now();
        member.createdAt = currentTime;
        member.updatedAt = currentTime;
        member.status = Status.ACTIVE;

        return member;
    }

    // ADMIN 권한 부여
    public static Member grantAdmin(Member member) {
        member.role = Role.ADMIN;

        LocalDateTime currentTime = LocalDateTime.now();
        member.updatedAt = currentTime;

        return member;
    }

    // 업데이트 메서드
    public static Member modifyMember(Member member, ModifyRequestDto request) {
        member.password = request.getPassword();
        member.profileImage = ProfileImage.updateProfileImage(member.profileImage, request.getProfileImage());
        member.userName = request.getUserName();
        member.introduction = request.getIntroduction();
        member.birth = request.getBirth();
        member.gender = request.getGender();
        member.phoneNumber = request.getPhoneNumber();

        LocalDateTime currentTime = LocalDateTime.now();
        member.updatedAt = currentTime;

        return member;
    }
}
