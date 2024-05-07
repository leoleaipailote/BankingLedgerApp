package dev.codescreen.model;

import java.time.LocalDateTime;

public class TransactionEvent {
    private String transactionId; // Corresponds to messageId
    private String userId; // User or account identifier
    private LocalDateTime timestamp;
    private Double amount;
    private String currency;
    private String debitOrCredit; // "DEBIT" or "CREDIT"
    private String status; // "APPROVED" or "DECLINED"
    private Balance balance;

    // Constructor
    public TransactionEvent(String transactionId, String userId, LocalDateTime timestamp, Double amount,
            String currency,
            String debitOrCredit, String status, Balance balance) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.timestamp = timestamp;
        this.amount = amount;
        this.currency = currency;
        this.debitOrCredit = debitOrCredit;
        this.status = status;
        this.balance = balance;
    }

    // Getters and Setters
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDebitOrCredit() {
        return debitOrCredit;
    }

    public void setDebitOrCredit(String debitOrCredit) {
        this.debitOrCredit = debitOrCredit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Balance getBalance() {
        return balance;
    }

    public void setBalance(Balance balance) {
        this.balance = balance;
    }
}
