package fete.be.domain.payment.application;

import fete.be.domain.payment.persistence.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TossService {

    private static final String TOSS_URL = "https://api.tosspayments.com/v1/payments/confirm";
    private final PaymentRepository paymentRepository;


    /**
     * ### 토스 페이먼츠 API 사용 방법
     * 1. 프론트 : 토스로 결제 요청
     * 2. 프론트 : 토스로부터 paymentKey, orderId, amount를 포함한 RedirectUrl을 전달 받는다.
     * 3. 프론트 : paymentKey, orderId, amount 이 3가지 정보를 백엔드로 넘겨준다.
     * 4. 백엔드 : 프론트로부터 받은 3가지 정보를 이용하여 토스에게 결제 승인을 보낸다.
     * 5. 백엔드 : 토스로부터 결제 승인 정보를 반환 받으며, 받은 정보로 DB를 업데이트 해주고 프론트로 응답을 보낸다.
     */

}
