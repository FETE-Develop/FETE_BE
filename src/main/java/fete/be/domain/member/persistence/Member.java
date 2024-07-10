package fete.be.domain.member.persistence;

import fete.be.global.util.Status;
import fete.be.domain.event.persistence.Participant;
import fete.be.domain.payment.Payment;
import fete.be.domain.poster.persistence.Poster;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @NotBlank
    @Column(nullable = false, length = 20)
    private String userName;

    @Enumerated(EnumType.STRING)
    private Role role;  // 권한

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Participant> participants = new ArrayList<>();  // 유저가 참여한 이벤트 리스트

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Payment> payments = new ArrayList<>();  // 유저의 결제 정보 리스트

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Poster> posters = new ArrayList<>();  // 유저가 등록한 프스터 리스트

    @Column(name = "created_at")
    private String createdAt;
    @Column(name = "updated_at")
    private String updatedAt;

    @Enumerated(EnumType.STRING)
    private Status status;


    // 생성 메서드
    public static Member createMember(String email, String password, String userName) {
        Member member = new Member();
        member.email = email;
        member.password = password;
        member.userName = userName;
        member.role = Role.USER;

        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        member.createdAt = currentTime;
        member.updatedAt = currentTime;
        member.status = Status.ACTIVE;

        return member;
    }

    // ADMIN 권한 부여
    public static Member grantAdmin(Member member) {
        member.role = Role.ADMIN;
        return member;
    }
}
