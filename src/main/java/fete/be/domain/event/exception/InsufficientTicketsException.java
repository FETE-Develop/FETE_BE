package fete.be.domain.event.exception;

public class InsufficientTicketsException extends RuntimeException {
    public InsufficientTicketsException() {
        super();
    }

    public InsufficientTicketsException(String message) {
        super(message);
    }

    public InsufficientTicketsException(String message, Throwable cause) {
        super(message, cause);
    }

    public InsufficientTicketsException(Throwable cause) {
        super(cause);
    }
}
