package fete.be.domain.event.exception;

public class IncorrectTicketTypeException extends RuntimeException{
    public IncorrectTicketTypeException() {
        super();
    }

    public IncorrectTicketTypeException(String message) {
        super(message);
    }

    public IncorrectTicketTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectTicketTypeException(Throwable cause) {
        super(cause);
    }
}
