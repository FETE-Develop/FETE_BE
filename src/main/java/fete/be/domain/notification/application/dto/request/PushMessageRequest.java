package fete.be.domain.notification.application.dto.request;

import lombok.Getter;

@Getter
public class PushMessageRequest {
    private String title;
    private String body;
}
