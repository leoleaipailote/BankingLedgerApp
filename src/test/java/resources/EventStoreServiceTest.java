package resources;

import dev.codescreen.model.Balance;
import dev.codescreen.model.TransactionEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import dev.codescreen.service.EventStoreService;

import java.time.LocalDateTime;
import java.util.List;

class EventStoreServiceTest {
    private EventStoreService eventStoreService;

    @BeforeEach
    void setUp() {
        eventStoreService = new EventStoreService();
    }

    @Test
    void testAddEvent() {
        LocalDateTime currTime = LocalDateTime.now();
        Balance balance = new Balance("0", "USD", "NA");
        TransactionEvent event = new TransactionEvent("message1", "user1", currTime, 100.0, "USD", "CREDIT", "NA",
                balance);
        eventStoreService.addEvent(event);
        assertEquals(1, eventStoreService.getEventsForUser("user1").size(), "Event should be added");
        assertTrue(eventStoreService.getEventsForUser("user1").contains(event),
                "Events list should contain the correct events for user1");
    }

    @Test
    void testGetEventsForUser() {
        LocalDateTime currTime = LocalDateTime.now();
        Balance balance = new Balance("0", "USD", "NA");
        TransactionEvent event1 = new TransactionEvent("message1", "user1", currTime, 300.0, "USD", "CREDIT", "NA",
                balance);
        TransactionEvent event2 = new TransactionEvent("message2", "user2", currTime, 50.0, "USD", "CREDIT", "NA",
                balance);
        TransactionEvent event3 = new TransactionEvent("message3", "user1", currTime, 250.0, "USD", "DEBIT", "NA",
                balance);

        eventStoreService.addEvent(event1);
        eventStoreService.addEvent(event2);
        eventStoreService.addEvent(event3);

        List<TransactionEvent> events = eventStoreService.getEventsForUser("user1");
        assertEquals(2, events.size(), "Should retrieve 2 events for user1");
        assertTrue(events.contains(event1) && events.contains(event3),
                "Events list should contain the correct events for user1");

        List<TransactionEvent> eventsUser2 = eventStoreService.getEventsForUser("user2");
        assertEquals(1, eventsUser2.size(), "Should retrieve 1 event for user2");
        assertTrue(eventsUser2.contains(event2),
                "Events list should contain the correct events for user1");
    }
}
