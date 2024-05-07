package dev.codescreen.model;

public class RequestBodies {
    private String messageId; // Include if messageId needs to be part of the load request
    private String userId;
    private TransactionAmount transactionAmount; // Embedded object for transaction details

    // Getters and setters
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public TransactionAmount getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(TransactionAmount transactionAmount) {
        this.transactionAmount = transactionAmount;
    }
}
