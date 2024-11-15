package fete.be.domain.event.application.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VerifyQRCodeRequest {
    private String value;
}
