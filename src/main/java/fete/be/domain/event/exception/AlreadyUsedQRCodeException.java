package fete.be.domain.event.exception;

public class AlreadyUsedQRCodeException extends RuntimeException{
    public AlreadyUsedQRCodeException(String message) {
        super(message);
    }
}
