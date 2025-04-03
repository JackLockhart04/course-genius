package nf.free.coursegenius.exceptions;

public class ApiException extends RuntimeException {
    private int errorCode;

    public ApiException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}