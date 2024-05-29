package fete.be.domain.member.persistence;

import fete.be.domain.Status;
import fete.be.domain.event.Participant;
import fete.be.domain.member.Role;
import fete.be.domain.payment.Payment;
import jakarta.persistence.*;
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

    private String email;
    private String password;
    private String userName;

    @Enumerated(EnumType.STRING)
    private Role role;  // 권한

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Participant> participants = new ArrayList<>();  // 유저가 참여한 이벤트 리스트

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Payment> payments = new ArrayList<>();  // 유저의 결제 정보 리스트

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
