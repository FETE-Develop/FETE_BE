package fete.be.domain.admin.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PaymentDto {
    private MemberDto member;  // 결제한 유저
    private String ticketType;  // 티켓 종류 - 얼리버드 / 현장구매 / 프로모션
    private int ticketPrice;  // 티켓 가격
    private Boolean isPaid;  // 결제 상태 : 지불 = true, 미지불 = false
    private int totalAmount;  // 총 결제 금액
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentAt;  // 결제 일자
    private Boolean isParticipated;  // 티켓 사용 여부
}
