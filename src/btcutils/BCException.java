package btcutils;

public class BCException extends RuntimeException {
    public BCException(String errorMessage) {
        super(errorMessage);
    }
}