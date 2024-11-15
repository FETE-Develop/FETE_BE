package fete.be.domain.event.exception;

public class NotFoundGenreException extends RuntimeException {
    public NotFoundGenreException(String message) {
        super(message);
    }
}
