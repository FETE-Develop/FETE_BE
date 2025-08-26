package fete.be.domain.event.exception;

public class NotFoundTicketException extends RuntimeException {
    public NotFoundTicketException(String message) {
        super(message);
    }
}
