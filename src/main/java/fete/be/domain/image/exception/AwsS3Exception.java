package fete.be.domain.image.exception;

public class AwsS3Exception extends RuntimeException {
    public AwsS3Exception(String message) {
        super(message);
    }
}