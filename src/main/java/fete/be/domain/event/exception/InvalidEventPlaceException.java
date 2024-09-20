package fete.be.domain.event.exception;

public class InvalidEventPlaceException extends RuntimeException{
    public InvalidEventPlaceException(String message) {
        super(message);
    }
}
