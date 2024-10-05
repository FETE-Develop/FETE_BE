package fete.be.domain.ticket.exception;

public class QRCodeGenerationException extends RuntimeException {
    public QRCodeGenerationException(String message) {
        super(message);
    }
}
