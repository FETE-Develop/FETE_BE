package fete.be.domain.payment.application;

import fete.be.domain.event.persistence.Ticket;
import fete.be.domain.payment.application.dto.request.TossCancelRequest;
import fete.be.domain.payment.application.dto.request.TossPaymentRequest;
import fete.be.domain.payment.application.dto.response.TossPaymentResponse;
import fete.be.domain.payment.exception.AlreadyUsedTicketException;
import fete.be.domain.payment.exception.InvalidCancelReasonException;
import fete.be.domain.payment.exception.InvalidTossResponseException;
import fete.be.domain.payment.persistence.Payment;
import fete.be.domain.payment.persistence.PaymentRepository;
import fete.be.domain.ticket.exception.InvalidRefundAmountException;
import fete.be.domain.ticket.persistence.Participant;
import fete.be.domain.ticket.persistence.ParticipantRepository;
import fete.be.global.util.ResponseMessage;
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
    private static final String TOSS_URL = "https://api.tosspayments.com/v1/payments/";

    private final RestTemplate restTemplate;
    private final PaymentRepository paymentRepository;
    private final ParticipantRepository participantRepository;


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

        // Toss API 헤더 생성
        HttpHeaders headers = makeHttpHeaders();

        // tossPaymentRequest를 이용해서 토스의 결제 승인 API를 호출할 요청 객체 만들기
        HttpEntity<TossPaymentRequest> requestHttpEntity = new HttpEntity<>(tossPaymentRequest, headers);

        log.info("Before Toss API Call");
        log.info("Headers={}", headers.toString());

        // 토스의 결제 승인 API 호출 준비
        String PAYMENT_URL = TOSS_URL + "confirm";
        TossPaymentResponse tossPaymentResponse = null;

        // 토스의 결제 승인 API 호출 후, TossPaymentResponse 응답 받기
        try {
            tossPaymentResponse = restTemplate.postForObject(PAYMENT_URL, requestHttpEntity, TossPaymentResponse.class);
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new InvalidTossResponseException(ResponseMessage.INVALID_TOSS_PAYMENT_API_RESPONSE.getMessage());
        }

        // 여러 개의 티켓 발급을 위해 participants에 복제
        for (Participant participant : participants) {
            // 토스에서 받은 응답으로 Payment 객체에 값 업데이트 이후, 저장
            Payment approvedPayment = Payment.updateTossPaymentInfo(participant.getPayment(), tossPaymentResponse);

            // 결제 완료로 변경
            Payment.completePayment(approvedPayment);
            paymentRepository.save(approvedPayment);
        }

        // 결제 결과가 반영된 Participant 반환
        return participants;
    }

    /**
     * ### 토스 결제 취소 API
     * 1. 프론트 : 취소할 티켓의 participantId, cancelReason, cancelAmount를 백엔드로 넘겨준다.
     * - cancelAmount는 부분 취소 결제 금액이다, 값을 전달하지 않으면 전액이 취소된다.
     * - cancelReason = 필수 전달 값
     * 2. 백엔드 : participantId를 통해 payment 객체에서 paymentKey 값을 가져온다.
     * 3. 백엔드 : 토스의 결제 취소 API를 호출한다.
     * - POST 메서드, https://api.tosspayments.com/v1/payments/{paymentKey}/cancel
     * - Authorization: Basic dGVzdF9za196WExrS0V5cE5BcldtbzUwblgzbG1lYXhZRzVSOg==
     * - Content-Type: application/json
     * - data '{"cancelReason":"고객 변심"}
     * 4.백엔드 : 토스로부터 TossPaymentResponse 형식의 응답을 받게 된다.
     * - 응답 속 cancels의 transactionKey를 저장하면 된다. -> 취소 거래를 구분하는 키이다.
     */
    @Transactional
    public String cancelPayment(Participant participant, Payment payment, String cancelReason) {
        log.info("Start cancelPayment");

        // cancelReason 검사
        if (cancelReason == null || cancelReason.isBlank()) {
            throw new InvalidCancelReasonException(ResponseMessage.TICKET_INVALID_CANCEL_REASON.getMessage());
        }

        // paymentKey 추출 후, URL 작업
        String paymentKey = payment.getPaymentKey();
        String CANCEL_URL = TOSS_URL + paymentKey + "/cancel";

        // Toss API 헤더 생성
        HttpHeaders headers = makeHttpHeaders();

        // 결제한 금액(=취소할 금액) 조회 후, TossCancelRequest 객체 생성
        int cancelAmount = payment.getTotalAmount();
        TossCancelRequest tossCancelRequest = new TossCancelRequest(cancelReason, cancelAmount);

        // tossCancelRequest를 이용해서 토스의 취소 API를 호출할 준비
        HttpEntity<TossCancelRequest> requestHttpEntity = new HttpEntity<>(tossCancelRequest, headers);

        // 토스 결제 취소 API 호출 이후, 응답 값 받기
        TossPaymentResponse tossPaymentResponse = null;
        try {
            tossPaymentResponse = restTemplate.postForObject(CANCEL_URL, requestHttpEntity, TossPaymentResponse.class);
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new InvalidTossResponseException(ResponseMessage.INVALID_TOSS_PAYMENT_API_RESPONSE.getMessage());
        }

        // Payment 객체에 취소 관련 정보 저장
        Payment canceledPayment = Payment.updateTossCancelInfo(payment, tossPaymentResponse.getLastTransactionKey(), cancelReason);

        // 이벤트 수익에서 취소된 금액 빼주기
        participant.updateProfit(cancelAmount * (-1));

        // 결제 취소 상태로 변경 -> 이벤트 수익 차감 이후에 호출되어야 함 (순서 중요)
        Payment.cancelPayment(canceledPayment);

        // 취소된 티켓 1장만큼 판매된 티켓 수량 감소
        updateSoldTicketCount(participant, canceledPayment);

        // DB 업데이트
        Payment savedPayment = paymentRepository.save(canceledPayment);

        // 취소 거래 키 반환
        return savedPayment.getLastTransactionKey();
    }

    /**
     * 무료 이벤트 티켓 취소
     */
    @Transactional
    public Long cancelFreeTicket(Participant participant, Payment payment, String cancelReason) {
        log.info("Start cancelFreeTicket");

        // 결제한 금액(=취소할 금액) 조회 후, 취소 금액이 0원이 맞는지 검사
        int cancelAmount = payment.getTotalAmount();
        if (cancelAmount != 0) {
            throw new InvalidRefundAmountException(ResponseMessage.TICKET_INVALID_AMOUNT.getMessage());
        }

        // Payment 객체에 취소 관련 정보 저장
        Payment canceledPayment = Payment.updateCancelInfo(payment, cancelReason);

        // 결제 취소 상태로 변경
        Payment.cancelPayment(canceledPayment);

        // 취소된 티켓 1장만큼 판매된 티켓 수량 감소
        updateSoldTicketCount(participant, canceledPayment);

        // DB 업데이트
        Payment savedPayment = paymentRepository.save(canceledPayment);

        // 취소된 Payment 아이디 반환
        return savedPayment.getPaymentId();
    }

    /**
     * 결제 금액에 따라 무료 티켓 취소 또는 유료 티켓 취소로 매핑
     */
    @Transactional
    public void cancelTicket(Long participantId, String cancelReason) {
        // Participant 객체 조회
        Participant participant = participantRepository.findById(participantId).orElseThrow(
                () -> new IllegalArgumentException("해당 티켓이 존재하지 않습니다.")
        );
        // Payment 객체 조회
        Payment payment = participant.getPayment();

        // 이미 사용한 티켓이라면 취소 불가
        if (participant.getIsParticipated()) {
            throw new AlreadyUsedTicketException(ResponseMessage.TICKET_ALREADY_USED.getMessage());
        }

        // 결제 취소할 금액 확인
        int cancelAmount = payment.getTotalAmount();
        if (cancelAmount == 0) {  // 무료 티켓 취소인 경우
            cancelFreeTicket(participant, payment, cancelReason);
        } else {  // 유료 티켓인 경우
            cancelPayment(participant, payment, cancelReason);
        }
    }

    /**
     * 판매된 티켓 수량에 취소된 티켓 수량 반영
     *
     * @param Participant participant
     * @param Payment     payment
     */
    private void updateSoldTicketCount(Participant participant, Payment payment) {
        // 취소된 티켓만큼 판매 티켓 수량 반영
        List<Ticket> tickets = participant.getEvent().getTickets();
        String ticketType = payment.getTicketType();

        // 취소된 티켓 개수를 반영
        for (Ticket ticket : tickets) {
            if (ticket.getTicketType().equals(ticketType)) {
                // 취소되었기 때문에 판매된 티켓 수량 값을 감소시켜줌
                Ticket.updateSoldTicketCount(ticket, -1);
            }
        }
    }

    /**
     * Toss API 헤더 생성
     * 1. secretKey와 콜론(:)을 Base64로 인코딩
     * 2. encodedKey를 Authorization Basic 설정
     * 3. contentType을 application/json으로 설정
     */
    private HttpHeaders makeHttpHeaders() {
        // secretKey와 콜론(:)을 Base64로 인코딩
        String key = secretKey + ":";
        String encodedKey = Base64.getEncoder().encodeToString(key.getBytes());

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodedKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
