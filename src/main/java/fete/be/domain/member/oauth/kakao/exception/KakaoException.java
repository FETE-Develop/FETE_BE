package fete.be.domain.member.oauth.kakao.exception;

public class KakaoException extends RuntimeException{

    public KakaoException() {
        super();
    }

    public KakaoException(String message) {
        super(message);
    }

    public KakaoException(String message, Throwable cause) {
        super(message, cause);
    }

    public KakaoException(Throwable cause) {
        super(cause);
    }
}
