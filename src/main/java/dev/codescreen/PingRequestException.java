package dev.codescreen;

public class PingRequestException extends RuntimeException {
    public PingRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
