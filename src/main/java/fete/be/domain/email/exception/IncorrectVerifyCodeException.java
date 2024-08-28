package fete.be.domain.email.exception;

public class IncorrectVerifyCodeException extends RuntimeException{
    public IncorrectVerifyCodeException() {
        super();
    }

    public IncorrectVerifyCodeException(String message) {
        super(message);
    }

    public IncorrectVerifyCodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectVerifyCodeException(Throwable cause) {
        super(cause);
    }
}
