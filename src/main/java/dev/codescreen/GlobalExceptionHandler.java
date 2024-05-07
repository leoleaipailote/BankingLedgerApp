package dev.codescreen;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PingRequestException.class)
    public ResponseEntity<Error> handlePingRequestException(PingRequestException e) {
        // Handle generic exceptions with a standard error response
        Error errorResponse = new Error(e.getMessage(), "500");
        return ResponseEntity.internalServerError().body(errorResponse);
    }

    @ExceptionHandler(LoadRequestException.class)
    public ResponseEntity<Error> handleRequestException(LoadRequestException e) {
        // Handle generic exceptions with a standard error response
        Error errorResponse = new Error(e.getMessage(), "501");
        return ResponseEntity.internalServerError().body(errorResponse);
    }

    @ExceptionHandler(AuthorizeRequestException.class)
    public ResponseEntity<Error> handleAuthorizeException(AuthorizeRequestException e) {
        // Handle generic exceptions with a standard error response
        Error errorResponse = new Error(e.getMessage(), "502");
        return ResponseEntity.internalServerError().body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleException(Exception e) {
        // Handle generic exceptions with a standard error response
        Error errorResponse = new Error("Internal server error", "500");
        return ResponseEntity.internalServerError().body(errorResponse);
    }

}
