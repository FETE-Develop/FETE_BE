package fete.be.domain.admin.application.dto.response;

import fete.be.domain.event.persistence.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {
    private String bankName;  // 은행
    private String accountNumber;  // 계좌번호
    private String accountHolder;  // 예금주

    public AccountDto(Event event) {
        this.bankName = event.getBankName();
        this.accountNumber = event.getAccountNumber();
        this.accountHolder = event.getAccountHolder();
    }
}
