package lissa.trading.user.service.exception;

public class OperationUnsupportedByBrokerException extends RuntimeException {
    public OperationUnsupportedByBrokerException(String message) {
        super(message);
    }
}
