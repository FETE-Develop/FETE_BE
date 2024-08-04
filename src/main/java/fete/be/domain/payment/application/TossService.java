package fete.be.domain.payment.application;

import fete.be.domain.member.application.MemberService;
import fete.be.domain.member.persistence.Member;
import fete.be.domain.payment.application.dto.request.TossPaymentRequest;
import fete.be.domain.payment.application.dto.response.TossPaymentResponse;
import fete.be.domain.payment.persistence.Payment;
import fete.be.domain.payment.persistence.PaymentRepository;
import fete.be.domain.ticket.persistence.Participant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class TossService {

    @Value("${toss.secretKey}")
    private String secretKey;
    private static final String TOSS_URL = "https://api.tosspayments.com/v1/payments/confirm";

    private final RestTemplate restTemplate;
    private final PaymentRepository paymentRepository;


    /**
     * ### 토스 페이먼츠 API 사용 방법
     * 1. 프론트 : 토스로 결제 요청
     * 2. 프론트 : 토스로부터 paymentKey, orderId, amount를 포함한 RedirectUrl을 전달 받는다.
     * 3. 프론트 : paymentKey, orderId, amount 이 3가지 정보를 백엔드로 넘겨준다.
     * 4. 백엔드 : 프론트로부터 받은 3가지 정보를 이용하여 토스에게 결제 승인을 보낸다.
     * 5. 백엔드 : 토스로부터 결제 승인 정보를 반환 받으며, 받은 정보로 DB를 업데이트 해주고 프론트로 응답을 보낸다.
     */
    @Transactional
    public List<Participant> executePayment(TossPaymentRequest tossPaymentRequest, List<Participant> participants) {
        log.info("Start executePayment");

        // secretKey와 콜론(:)을 Base64로 인코딩
        String key = secretKey + ":";
        String encodedKey = Base64.getEncoder().encodeToString(key.getBytes());

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodedKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // tossPaymentRequest를 이용해서 토스의 결제 승인 API를 호출할 요청 객체 만들기
        HttpEntity<TossPaymentRequest> requestHttpEntity = new HttpEntity<>(tossPaymentRequest, headers);

        log.info("Before Toss API Call");
        log.info("Headers={}", headers.toString());

        // 토스의 결제 승인 API 호출 후, TossPaymentResponse로 응답 받기
        TossPaymentResponse tossPaymentResponse = restTemplate.postForObject(TOSS_URL, requestHttpEntity, TossPaymentResponse.class);

        log.info("TossPaymentResponse={}", tossPaymentResponse);

        // 여러 개의 티켓 발급을 위해 participants에 복제
        for (Participant participant : participants) {
            // 토스에서 받은 응답으로 Payment 객체에 값 업데이트 이후, 저장
            Payment approvedPayment = Payment.updateTossFields(participant.getPayment(), tossPaymentResponse);

            // 결제 완료로 변경
            Payment.completePayment(approvedPayment);
            paymentRepository.save(approvedPayment);
        }

        // 결제 결과가 반영된 Participant 반환
        return participants;
    }
}
