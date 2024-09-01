package fete.be.domain.notification.application;

import fete.be.domain.member.application.MemberService;
import fete.be.domain.member.persistence.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationService {

    private final MemberService memberService;


    /**
     * FCM 토큰 저장
     */
    @Transactional
    public Long storeToken(String fcmToken) {
        // 유저 조회
        Member member = memberService.findMemberByEmail();
        // FCM 토큰 저장
        Member savedMember = Member.storeToken(member, fcmToken);

        return savedMember.getMemberId();
    }
}
