package fete.be.domain.member;

import fete.be.domain.Status;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    private String email;
    private String password;
    private String userName;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String createdAt;
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

}
