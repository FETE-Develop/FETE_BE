package fete.be.domain.admin.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetPaymentsResponse {
    private List<PaymentDto> payments;  // 유저들의 결제 기록
    private int totalProfit;  // 이벤트의 총 수익
}
