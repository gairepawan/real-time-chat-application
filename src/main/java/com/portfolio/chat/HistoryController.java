package com.portfolio.chat;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class HistoryController {
    private final ChatService service;
    public HistoryController(ChatService service) { this.service = service; }

    @GetMapping("/{room}/messages")
    public ResponseEntity<List<ChatMessage>> messages(@PathVariable String room) {
        return ResponseEntity.ok(service.history(room));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> badRoom(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}