package me.exrates.scheduleservice.exceptions;

public class WalletsApiException extends RuntimeException {

    public WalletsApiException() {
        super();
    }

    public WalletsApiException(String message) {
        super(message);
    }

    public WalletsApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
