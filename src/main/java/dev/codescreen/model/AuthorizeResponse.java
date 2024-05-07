package dev.codescreen.model;

public class AuthorizeResponse {
    private String messageId;
    private String userId;
    private String responseCode;
    private Balance balance;

    public AuthorizeResponse(String messageId, String userId, String responseCode, Balance balance) {
        this.messageId = messageId;
        this.userId = userId;
        this.responseCode = responseCode;
        this.balance = balance;
    }

    public String getmessageId() {
        return messageId;
    }

    public String getUserId() {
        return userId;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public Balance getBalance() {
        return balance;
    }
}
