package fete.be.domain.payment.exception;

public class InvalidPaymentStatusException extends RuntimeException{
    public InvalidPaymentStatusException(String message) {
        super(message);
    }
}
