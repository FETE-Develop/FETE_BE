package fete.be.domain.payment.exception;

public class NotFoundCardCodeException extends RuntimeException {
    public NotFoundCardCodeException(String message) {
        super(message);
    }
}
