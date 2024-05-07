package dev.codescreen.service;

import dev.codescreen.model.TransactionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class EventStoreService {
    private final List<TransactionEvent> eventStore = new ArrayList<>();

    /**
     * Adds a transaction event to the event store.
     * 
     * @param event The transaction event to be added.
     */
    public void addEvent(TransactionEvent event) {
        eventStore.add(event);
    }

    /**
     * Retrieves all transaction events associated with a specific user.
     * 
     * @param userId The user ID for which transaction events are to be retrieved.
     * @return A list of transaction events for the specified user.
     */
    public List<TransactionEvent> getEventsForUser(String userId) {
        // Filter and return events for a specific user based on userId

        return eventStore.stream()
                .filter(event -> event.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public int size() {
        return eventStore.size();
    }
}
