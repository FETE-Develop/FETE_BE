package fete.be.domain.event.application.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantDto {
    Long participantId;
    Long memberId;
    Long eventId;
    Long paymentId;
}
