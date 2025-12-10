package fete.be.domain.event.exception;

public class RedissonException extends RuntimeException{
    public RedissonException(String message) {
        super(message);
    }
}
