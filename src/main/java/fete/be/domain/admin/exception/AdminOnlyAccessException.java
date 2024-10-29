package fete.be.domain.admin.exception;

public class AdminOnlyAccessException extends RuntimeException{
    public AdminOnlyAccessException(String message) {
        super(message);
    }
}
