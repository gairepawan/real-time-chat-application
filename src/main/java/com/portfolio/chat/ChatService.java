package com.portfolio.chat;

import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Bounded in-memory message history; reset on restart. */
@Service
public class ChatService {
    private static final int MAX_PER_ROOM = 100;
    private final Map<String, Deque<ChatMessage>> rooms = new ConcurrentHashMap<>();

    public ChatMessage save(IncomingMessage incoming) {
        if (incoming == null || incoming.room() == null || incoming.sender() == null || incoming.text() == null)
            throw new IllegalArgumentException("Room, sender and text are required");
        String room = incoming.room().strip().toLowerCase();
        String sender = incoming.sender().strip();
        String body = incoming.text().strip();
        if (!room.matches("[a-z0-9_-]{1,24}")) throw new IllegalArgumentException("Invalid room name");
        if (sender.isBlank() || sender.length() > 30 || body.isBlank() || body.length() > 500)
            throw new IllegalArgumentException("Invalid sender or message length");
        ChatMessage message = new ChatMessage(room, sender, body, Instant.now());
        Deque<ChatMessage> history = rooms.computeIfAbsent(room, ignored -> new ArrayDeque<>());
        synchronized (history) {
            if (history.size() == MAX_PER_ROOM) history.removeFirst();
            history.addLast(message);
        }
        return message;
    }

    public List<ChatMessage> history(String room) {
        if (room == null || !room.matches("[a-z0-9_-]{1,24}")) throw new IllegalArgumentException("Invalid room name");
        Deque<ChatMessage> history = rooms.get(room);
        if (history == null) return List.of();
        synchronized (history) { return List.copyOf(new ArrayList<>(history)); }
    }
}