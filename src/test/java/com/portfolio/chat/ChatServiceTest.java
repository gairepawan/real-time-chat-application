package com.portfolio.chat;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ChatServiceTest {
    @Test void savesAndRetrievesMessages() {
        ChatService service = new ChatService();
        ChatMessage result = service.save(new IncomingMessage("General", "Alex", "Hello"));
        assertEquals("general", result.room());
        assertEquals("Hello", service.history("general").getFirst().text());
    }
    @Test void keepsOnlyLatestHundredMessages() {
        ChatService service = new ChatService();
        for (int i = 0; i < 105; i++) service.save(new IncomingMessage("general", "Alex", "msg" + i));
        assertEquals(100, service.history("general").size());
        assertEquals("msg5", service.history("general").getFirst().text());
        assertEquals("msg104", service.history("general").getLast().text());
    }
    @Test void separatesRooms() {
        ChatService service = new ChatService();
        service.save(new IncomingMessage("general", "Alex", "One"));
        service.save(new IncomingMessage("tech", "Sam", "Two"));
        assertEquals(1, service.history("general").size());
        assertEquals("Two", service.history("tech").getFirst().text());
    }
    @Test void rejectsInvalidMessagesAndRoomNames() {
        ChatService service = new ChatService();
        assertThrows(IllegalArgumentException.class, () -> service.save(new IncomingMessage("../bad", "Alex", "hi")));
        assertThrows(IllegalArgumentException.class, () -> service.save(new IncomingMessage("general", " ", "hi")));
        assertThrows(IllegalArgumentException.class, () -> service.save(new IncomingMessage("general", "Alex", " ")));
        assertThrows(IllegalArgumentException.class, () -> service.history("../bad"));
    }
    @Test void historyIsImmutableSnapshot() {
        ChatService service = new ChatService();
        service.save(new IncomingMessage("general", "A", "First"));
        var snapshot = service.history("general");
        service.save(new IncomingMessage("general", "A", "Second"));
        assertEquals(1, snapshot.size());
        assertThrows(UnsupportedOperationException.class, snapshot::clear);
    }
}