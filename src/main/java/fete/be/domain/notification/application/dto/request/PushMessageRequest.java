package fete.be.domain.notification.application.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PushMessageRequest {
    private String title;
    private String body;
}
