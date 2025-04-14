package fete.be.domain.event.application.dto.request;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class VerifyQRCodeRequest {
    private ParticipantDto participantDto;
    @Nullable
    private String managerCode;
}
