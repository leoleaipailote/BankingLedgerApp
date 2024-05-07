package dev.codescreen.service;

import dev.codescreen.model.TransactionEvent;
import dev.codescreen.model.Balance;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private final EventStoreService eventStoreService;

    public TransactionService(EventStoreService eventStoreService) {
        this.eventStoreService = eventStoreService;
    }

    public TransactionEvent processLoad(String transactionId, String userId,
            Double amount, String currency, String debitOrCredit) {
        String status = "NA";
        LocalDateTime timestamp = LocalDateTime.now();
        double balanceAmount = getCurrentBalance(userId);
        Balance balance = new Balance(Double.toString(balanceAmount), currency, debitOrCredit);

        TransactionEvent event = new TransactionEvent(transactionId, userId, timestamp, amount,
                currency, debitOrCredit, status, balance);
        eventStoreService.addEvent(event);
        balanceAmount = getCurrentBalance(userId);
        balance.setAmount(Double.toString(balanceAmount));
        event.setBalance(balance);
        return event;
    }

    public TransactionEvent processAuthorization(String transactionId, String userId,
            Double amount, String currency, String debitOrCredit) {
        String status;
        double currentBalance = getCurrentBalance(userId);
        LocalDateTime timestamp = LocalDateTime.now();
        // Check if the account has enough funds
        if (currentBalance >= amount)
            status = "APPROVED";
        else
            status = "DECLINED";

        double balanceAmount = getCurrentBalance(userId);
        Balance balance = new Balance(Double.toString(balanceAmount), currency, debitOrCredit);
        TransactionEvent event = new TransactionEvent(transactionId, userId, timestamp, amount,
                currency, debitOrCredit, status, balance);
        eventStoreService.addEvent(event);
        balanceAmount = getCurrentBalance(userId);
        balance.setAmount(Double.toString(balanceAmount));
        event.setBalance(balance);
        return event;
    }

    public double getCurrentBalance(String userId) {
        double currentBalance = 0;
        List<TransactionEvent> events = eventStoreService.getEventsForUser(userId);
        for (TransactionEvent event : events) {
            if (event.getDebitOrCredit().equals("CREDIT"))
                currentBalance += event.getAmount();
            else if (event.getStatus().equals("APPROVED"))
                currentBalance -= event.getAmount();
        }
        return currentBalance;
    }
}
