package fete.be.domain.image.exception;

public class DuplicateImageException extends RuntimeException {
    public DuplicateImageException(String message) {
        super(message);
    }
}
