package dev.codescreen.model;

public class Balance {
    private String amount;
    private String currency;
    private String debitOrCredit;

    public Balance(String amount, String currency, String debitOrCredit) {
        this.amount = amount;
        this.currency = currency;
        this.debitOrCredit = debitOrCredit;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getDebitOrCredit() {
        return debitOrCredit;
    }

}
