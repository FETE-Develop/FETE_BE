package fete.be.domain.admin.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class AccountDto {
    private String bankName;  // 은행
    private String accountNumber;  // 계좌번호
}
