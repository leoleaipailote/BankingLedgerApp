package dev.codescreen.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.codescreen.PingRequestException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api")
public class SystemController {

    @GetMapping("/ping")
    public ResponseEntity<PingResponse> ping() {
        try {
            String serverTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME); // Ensure correct format
            return ResponseEntity.ok(new PingResponse(serverTime));
        } catch (Exception e) {
            // Re-throw and let the global handler manage it
            throw new PingRequestException("Failed to process ping request", e);
        }
    }

    // Inner class for the Ping response to match the schema
    public static class PingResponse {
        private String serverTime;

        public PingResponse(String serverTime) {
            this.serverTime = serverTime;
        }

        public String getServerTime() {
            return serverTime;
        }
    }
}
