package fete.be.domain.event.exception;

public class IncorrectQRCodeException extends RuntimeException{
    public IncorrectQRCodeException(String message) {
        super(message);
    }
}
