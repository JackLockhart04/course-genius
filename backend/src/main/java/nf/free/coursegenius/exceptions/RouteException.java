package nf.free.coursegenius.exceptions;

public class RouteException extends RuntimeException {
    private int errorCode;

    public RouteException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}