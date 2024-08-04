package fete.be.domain.event.exception;

public class IncorrectPaymentAmountException extends RuntimeException{
    public IncorrectPaymentAmountException() {
        super();
    }

    public IncorrectPaymentAmountException(String message) {
        super(message);
    }

    public IncorrectPaymentAmountException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectPaymentAmountException(Throwable cause) {
        super(cause);
    }
}
