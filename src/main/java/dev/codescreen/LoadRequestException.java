package dev.codescreen;

public class LoadRequestException extends RuntimeException {
    public LoadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
