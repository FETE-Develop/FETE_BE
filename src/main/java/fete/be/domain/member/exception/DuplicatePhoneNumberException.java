package fete.be.domain.member.exception;

public class DuplicatePhoneNumberException extends RuntimeException{
    public DuplicatePhoneNumberException(String message) {
        super(message);
    }
}
