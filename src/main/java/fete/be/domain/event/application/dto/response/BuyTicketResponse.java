package fete.be.domain.event.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class BuyTicketResponse {
    private List<String> qrCodes;
}
