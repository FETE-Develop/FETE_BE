package fete.be.domain.notification.application;

import com.google.firebase.messaging.*;
import fete.be.domain.member.application.MemberService;
import fete.be.domain.member.persistence.Member;
import fete.be.domain.notification.application.dto.request.PushMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationService {

    private static final int MAX_SEND_TOKEN_LENGTH = 500;
    private final MemberService memberService;


    /**
     * FCM 토큰 저장
     */
    @Transactional
    public Long storeToken(String fcmToken) {
        // 유저를 조회하여, 해당 유저의 FCM 토큰 값 저장
        Member member = memberService.findMemberByEmail();
        Member savedMember = Member.storeToken(member, fcmToken);

        return savedMember.getMemberId();
    }

    /**
     * 단일 푸시 알림 전송
     * 응답 메시지 예시
     *     {
     *       "name":"projects/myproject-b5ae1/messages/0:1500415314455276%31bd1c9631bd1c96"
     *     }
     */
    public String sendOne(String fcmToken, PushMessageRequest request) throws FirebaseMessagingException {
        // 푸시 알림 메시지 생성
        Message message = Message.builder()
                .putData("title", request.getTitle())
                .putData("body", request.getBody())
                .setToken(fcmToken)
                .build();

        // 메시지 전송 후, 전송된 메시지 ID 응답
        String response = FirebaseMessaging.getInstance().send(message);
        return response;
    }

    /**
     * 여러 명 푸시 알림 전송
     * - 호출당 최대 500개의 기기까지 지정 가능
     * - BatchResponse : 반환 값은 응답 목록이 입력 토큰 순서와 일치
     *   -> 오류가 발생한 토큰을 확인하려는 경우에 유용함
     */
    public BatchResponse sendAll(PushMessageRequest request) throws FirebaseMessagingException {
        List<String> allTokens = memberService.getAllTokens();
        List<BatchResponse> responses = new ArrayList<>();

        for (int i = 0; i < allTokens.size(); i += MAX_SEND_TOKEN_LENGTH) {
            // 토큰을 500개씩 잘라서 전송
            List<String> fcmTokens = allTokens.subList(i, Math.min(i + MAX_SEND_TOKEN_LENGTH, allTokens.size()));

            // 푸시 알림 메시지 생성
            MulticastMessage message = MulticastMessage.builder()
                    .putData("title", request.getTitle())
                    .putData("body", request.getBody())
                    .addAllTokens(fcmTokens)
                    .build();

            // 메시지 전송 후, 전송된 메시지 ID 응답
            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
            responses.add(response);
        }

        // 마지막 응답을 반환
        return responses.get(responses.size() - 1);
    }
}
