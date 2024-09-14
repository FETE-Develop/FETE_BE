package fete.be.domain.image.exception;

public class UploadErrorException extends RuntimeException {
    public UploadErrorException(String message) {
        super(message);
    }
}
