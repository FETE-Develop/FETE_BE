package fete.be.domain.member.exception;

public class InvalidJwtTokenException extends RuntimeException{
    public InvalidJwtTokenException(String message) {
        super(message);
    }
}
