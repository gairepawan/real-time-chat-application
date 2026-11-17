package com.portfolio.chat;

import java.time.Instant;

public record ChatMessage(String room, String sender, String text, Instant sentAt) {}