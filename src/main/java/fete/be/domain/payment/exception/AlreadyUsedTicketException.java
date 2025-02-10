package fete.be.domain.payment.exception;

public class AlreadyUsedTicketException extends RuntimeException {
    public AlreadyUsedTicketException(String message) {
        super(message);
    }
}
