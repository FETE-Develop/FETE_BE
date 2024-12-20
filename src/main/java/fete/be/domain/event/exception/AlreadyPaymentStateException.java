package fete.be.domain.event.exception;

public class AlreadyPaymentStateException extends RuntimeException{
    public AlreadyPaymentStateException(String message) {
        super(message);
    }
}
