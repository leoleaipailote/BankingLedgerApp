package dev.codescreen.model;

public class LoadResponse {
    private String messageId;
    private String userId;
    private Balance balance;

    public LoadResponse(String messageId, String userId, Balance balance) {
        this.messageId = messageId;
        this.userId = userId;
        this.balance = balance;
    }

    public String getmessageId() {
        return messageId;
    }

    public String getUserId() {
        return userId;
    }

    public Balance getBalance() {
        return balance;
    }
}
