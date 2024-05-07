package dev.codescreen.controller;

import dev.codescreen.model.LoadResponse;
import dev.codescreen.model.RequestBodies;
import dev.codescreen.model.TransactionEvent;
import dev.codescreen.LoadRequestException;
import dev.codescreen.AuthorizeRequestException;
import dev.codescreen.model.AuthorizeResponse;
import dev.codescreen.service.TransactionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PutMapping("/load/{messageId}")
    public ResponseEntity<LoadResponse> loadFunds(@PathVariable String messageId,
            @RequestBody RequestBodies requestBody) {
        try {
            Double amount = Double.parseDouble(requestBody.getTransactionAmount().getAmount());
            // Returns a server error if amount value is negative
            if (amount < 0)
                throw new IllegalArgumentException("Amount cannot be negative");
            String currency = requestBody.getTransactionAmount().getCurrency();
            String debitOrCredit = requestBody.getTransactionAmount().getDebitOrCredit();
            // Returns a server error if this field is anything other than CREDIT
            if (!debitOrCredit.equals("CREDIT"))
                throw new IllegalArgumentException("This field has to be CREDIT");
            String userId = requestBody.getUserId();
            // Process the load request and uses the returned transaction event to form
            // response
            TransactionEvent event = transactionService.processLoad(messageId, userId, amount,
                    currency, debitOrCredit);
            return ResponseEntity
                    .status(201).body(new LoadResponse(messageId, userId, event.getBalance()));
        } catch (Exception e) {
            throw new LoadRequestException("Failed to process load request", e);
        }
    }

    @PutMapping("/authorization/{messageId}")
    public ResponseEntity<AuthorizeResponse> authorizeTransaction(@PathVariable String messageId,
            @RequestBody RequestBodies requestBody) {
        try {
            Double amount = Double.parseDouble(requestBody.getTransactionAmount().getAmount());
            if (amount < 0)
                throw new IllegalArgumentException("Amount cannot be negative");
            String currency = requestBody.getTransactionAmount().getCurrency();
            String debitOrCredit = requestBody.getTransactionAmount().getDebitOrCredit();
            if (!debitOrCredit.equals("DEBIT"))
                throw new IllegalArgumentException("This field has to be DEBIT");
            String userId = requestBody.getUserId();
            // Process the authorize request and uses the returned transaction event to form
            // response
            TransactionEvent event = transactionService.processAuthorization(messageId, userId, amount,
                    currency, debitOrCredit);
            return ResponseEntity
                    .status(201).body(new AuthorizeResponse(messageId, userId, event.getStatus(), event.getBalance()));
        } catch (Exception e) {
            throw new AuthorizeRequestException("Failed to process authorization request", e);
        }

    }
}
