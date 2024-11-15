package fete.be.domain.event.persistence;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class TicketInfoDto {
    private String ticketType;  // 티켓 종류 - 얼리버드 / 현장구매 / 프로모션
    private String description;  // 티켓 설명
    private int ticketPrice;  // 티켓 가격
    private int maxTicketCount;  // 티켓의 최대 개수
}
