package fete.be.domain.member.exception;

public class BlockedUserException extends RuntimeException{
    public BlockedUserException(String message) {
        super(message);
    }
}
