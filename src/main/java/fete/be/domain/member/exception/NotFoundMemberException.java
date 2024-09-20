package fete.be.domain.member.exception;

public class NotFoundMemberException extends RuntimeException{
    public NotFoundMemberException(String message) {
        super(message);
    }
}
