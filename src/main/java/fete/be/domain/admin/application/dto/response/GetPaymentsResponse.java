package fete.be.domain.admin.application.dto.response;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetPaymentsResponse {
    private List<PaymentDto> payments;  // 유저들의 결제 기록
    private int totalProfit;  // 이벤트의 총 수익
    private AccountDto account;  // 이벤트 담당자의 계좌 정보
}
