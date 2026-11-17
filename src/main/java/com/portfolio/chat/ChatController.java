package com.portfolio.chat;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messaging;

    public ChatController(ChatService chatService, SimpMessagingTemplate messaging) {
        this.chatService = chatService;
        this.messaging = messaging;
    }

    @MessageMapping("/chat.send")
    public void send(IncomingMessage incoming) {
        ChatMessage message = chatService.save(incoming);
        messaging.convertAndSend("/topic/rooms/" + message.room(), message);
    }
}