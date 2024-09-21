package fete.be.domain.image.exception;

public class NotFoundFileInS3Exception extends RuntimeException {
    public NotFoundFileInS3Exception(String message) {
        super(message);
    }
}
