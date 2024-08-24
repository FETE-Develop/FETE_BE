package fete.be.domain.member.persistence;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class BlockedMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blocked_member_id")
    private Long blockedMemberId;

    private String phoneNumber;  // 차단할 휴대전화 번호


    // 생성 메서드
    public static BlockedMember createBlockedMember(String phoneNumber) {
        BlockedMember blockedMember = new BlockedMember();
        blockedMember.phoneNumber = phoneNumber;

        return blockedMember;
    }
}
