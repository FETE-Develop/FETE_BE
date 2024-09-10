package fete.be.domain.payment.exception;

public class InvalidCancelReasonException extends RuntimeException {
    public InvalidCancelReasonException(String message) {
        super(message);
    }
}
