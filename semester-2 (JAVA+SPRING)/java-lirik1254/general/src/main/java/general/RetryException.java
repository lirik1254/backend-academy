package general;

public class RetryException extends RuntimeException {
    public RetryException(String message) {
        super(message);
    }
}
