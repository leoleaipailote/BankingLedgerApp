package dev.codescreen;

public class AuthorizeRequestException extends RuntimeException {
    public AuthorizeRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
