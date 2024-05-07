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
            String currency = requestBody.getTransactionAmount().getCurrency();
            String debitOrCredit = requestBody.getTransactionAmount().getDebitOrCredit();
            String userId = requestBody.getUserId();
            TransactionEvent event = transactionService.processLoad(messageId, userId, amount,
                    currency, debitOrCredit);
            return ResponseEntity
                    .status(200).body(new LoadResponse(messageId, userId, event.getBalance()));
        } catch (Exception e) {
            throw new LoadRequestException("Failed to process load request", e);
        }
    }

    @PutMapping("/authorization/{messageId}")
    public ResponseEntity<AuthorizeResponse> authorizeTransaction(@PathVariable String messageId,
            @RequestBody RequestBodies requestBody) {
        try {
            Double amount = Double.parseDouble(requestBody.getTransactionAmount().getAmount());
            String currency = requestBody.getTransactionAmount().getCurrency();
            String debitOrCredit = requestBody.getTransactionAmount().getDebitOrCredit();
            String userId = requestBody.getUserId();
            TransactionEvent event = transactionService.processAuthorization(messageId, userId, amount,
                    currency, debitOrCredit);
            return ResponseEntity
                    .status(201).body(new AuthorizeResponse(messageId, userId, event.getStatus(), event.getBalance()));
        } catch (Exception e) {
            throw new AuthorizeRequestException("Failed to process authorization request", e);
        }

    }
}
