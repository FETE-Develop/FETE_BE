package fete.be.domain.member;

import jakarta.persistence.*;
import lombok.Getter;

import javax.management.relation.Role;

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
    private String status;

    public static Member createMember(String email, String password, String userName) {
        Member member = new Member();
        member.email = email;
        member.password = password;
        member.userName = userName;
    }

}
