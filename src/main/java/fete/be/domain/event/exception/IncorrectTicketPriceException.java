package fete.be.domain.event.exception;

public class IncorrectTicketPriceException extends RuntimeException{
    public IncorrectTicketPriceException() {
        super();
    }

    public IncorrectTicketPriceException(String message) {
        super(message);
    }

    public IncorrectTicketPriceException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectTicketPriceException(Throwable cause) {
        super(cause);
    }
}
