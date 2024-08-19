package lissa.trading.user.service.exception;

public class TokenEncryptionException extends RuntimeException {
    public TokenEncryptionException(String message) {
        super(message);
    }

    public TokenEncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
