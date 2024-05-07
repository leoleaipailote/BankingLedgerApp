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
        // Calculating the existing balance
        Balance balance = new Balance(Double.toString(balanceAmount), currency, debitOrCredit);
        // Create a new TransactionEvent from parameters and add to eventStore
        TransactionEvent event = new TransactionEvent(transactionId, userId, timestamp, amount,
                currency, debitOrCredit, status, balance);
        eventStoreService.addEvent(event);
        // Recalculate and store new balance after addition of new event
        balanceAmount = getCurrentBalance(userId);
        balance.setAmount(Double.toString(balanceAmount));
        event.setBalance(balance);
        return event;
    }

    public TransactionEvent processAuthorization(String transactionId, String userId,
            Double amount, String currency, String debitOrCredit) {
        String status;
        // Calculating the existing balance
        double currentBalance = getCurrentBalance(userId);
        LocalDateTime timestamp = LocalDateTime.now();
        // Check if the account has enough funds
        if (currentBalance >= amount)
            status = "APPROVED";
        else
            status = "DECLINED";

        double balanceAmount = getCurrentBalance(userId);
        // Create a new TransactionEvent from parameters including whether request was
        // authorized and add to eventStore
        Balance balance = new Balance(Double.toString(balanceAmount), currency, debitOrCredit);
        TransactionEvent event = new TransactionEvent(transactionId, userId, timestamp, amount,
                currency, debitOrCredit, status, balance);
        eventStoreService.addEvent(event);
        // Recalculate and store new balance after addition of new event
        balanceAmount = getCurrentBalance(userId);
        balance.setAmount(Double.toString(balanceAmount));
        event.setBalance(balance);
        return event;
    }

    // Finds the balance by iterating through the eventStore and finding events
    // associated with the userId
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
