package com.portfolio.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record IncomingMessage(
        @NotBlank @Size(max = 24) String room,
        @NotBlank @Size(max = 30) String sender,
        @NotBlank @Size(max = 500) String text) {}