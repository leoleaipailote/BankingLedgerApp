package resources;

import dev.codescreen.model.TransactionEvent;
import dev.codescreen.model.Balance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import dev.codescreen.service.EventStoreService;
import dev.codescreen.service.TransactionService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class TransactionServiceTest {
    @Mock
    private EventStoreService eventStoreService;
    private TransactionService transactionService;
    private List<TransactionEvent> eventList;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        transactionService = new TransactionService(eventStoreService);

        eventList = new ArrayList<>();
        eventList.add(new TransactionEvent("initTx", "user1", LocalDateTime.now(), 50.0, "USD", "CREDIT", "NA",
                new Balance("50.0", "USD", "CREDIT")));

        // Setup dynamic mock responses for event retrieval
        when(eventStoreService.getEventsForUser(anyString())).thenAnswer(invocation -> new ArrayList<>(eventList));
        doAnswer(invocation -> {
            TransactionEvent event = invocation.getArgument(0);
            eventList.add(event);
            return null;
        }).when(eventStoreService).addEvent(any(TransactionEvent.class));
    }

    @Test
    void testGetCurrentBalance() {
        String userId = "user1";
        List<TransactionEvent> events = Arrays.asList(
                new TransactionEvent("tx1", userId, LocalDateTime.now(), 100.0, "USD", "CREDIT", "NA",
                        new Balance("0", "USD", "CREDIT")),
                new TransactionEvent("tx2", userId, LocalDateTime.now(), 50.0, "USD", "DEBIT", "APPROVED",
                        new Balance("100", "USD", "DEBIT")));

        when(eventStoreService.getEventsForUser(userId)).thenReturn(events);

        double expectedBalance = 50.0; // 100 (credit) - 50 (approved debit)
        double actualBalance = transactionService.getCurrentBalance(userId);

        assertEquals(expectedBalance, actualBalance, 0.01, "The calculated balance should be correct.");
    }

    @Test
    void testGetCurrentBalanceInsufficient() {
        String userId = "user1";
        List<TransactionEvent> events = Arrays.asList(
                new TransactionEvent("tx2", userId, LocalDateTime.now(), 50.0, "USD", "DEBIT", "DECLINED",
                        new Balance("0.0", "USD", "DEBIT")));

        when(eventStoreService.getEventsForUser(userId)).thenReturn(events);

        double expectedBalance = 0.0; // Balance should be unchanged due to authorization status being declined
        double actualBalance = transactionService.getCurrentBalance(userId);
        assertEquals(expectedBalance, actualBalance, 0.01, "The calculated balance should be correct.");
    }

    @Test
    void testProcessLoad() {
        // Arrange
        String userId = "user1";
        String transactionId = "tx123";
        Double amount = 100.0;
        String currency = "USD";
        String debitOrCredit = "CREDIT";

        // Act - Process a load transaction
        TransactionEvent result = transactionService.processLoad(transactionId, userId, amount, currency,
                debitOrCredit);

        // Assert - Check the properties of the returned transaction event
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(amount, result.getAmount());
        // Check if the balance has been updated correctly
        assertEquals("150.0", result.getBalance().getAmount());
    }

    @Test
    void testProcessAuthorization() {
        // Arrange
        String userId = "user1";
        String transactionId = "tx123";
        Double amount = 25.0;
        String currency = "USD";
        String debitOrCredit = "DEBIT";

        // Act - Process a load transaction
        TransactionEvent result = transactionService.processAuthorization(transactionId, userId, amount, currency,
                debitOrCredit);

        // Assert - Check the properties of the returned transaction event
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(amount, result.getAmount());
        assertEquals("25.0", result.getBalance().getAmount());
    }

    @Test
    void testProcessAuthorizationDeclined() {
        // Arrange
        String userId = "user1";
        String transactionId = "tx123";
        Double amount = 100.0;
        String currency = "USD";
        String debitOrCredit = "DEBIT";

        // Act - Process a load transaction
        TransactionEvent result = transactionService.processAuthorization(transactionId, userId, amount, currency,
                debitOrCredit);

        // Assert - Check the properties of the returned transaction event
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(amount, result.getAmount());
        assertEquals("50.0", result.getBalance().getAmount());
    }

}
