package fete.be.domain.event.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@AllArgsConstructor
@ToString
public class BuyTicketResponse {
    private List<String> qrCodes;
}
