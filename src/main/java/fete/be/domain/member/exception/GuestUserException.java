package fete.be.domain.member.exception;

public class GuestUserException extends RuntimeException{
    public GuestUserException(String message) {
        super(message);
    }
}
