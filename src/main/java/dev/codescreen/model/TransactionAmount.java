package dev.codescreen.model;

public class TransactionAmount {
    private String amount;
    private String currency;
    private String debitOrCredit; // Optional, include if needed for loads

    // Getters and setters
    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
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
}
