package fete.be.domain.payment.exception;

public class NotFoundTicketStatusException extends RuntimeException {
    public NotFoundTicketStatusException(String message) {
        super(message);
    }
}
