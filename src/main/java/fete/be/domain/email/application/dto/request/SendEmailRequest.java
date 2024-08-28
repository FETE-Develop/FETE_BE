package fete.be.domain.email.application.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SendEmailRequest {
    @Email
    private String email;
}
