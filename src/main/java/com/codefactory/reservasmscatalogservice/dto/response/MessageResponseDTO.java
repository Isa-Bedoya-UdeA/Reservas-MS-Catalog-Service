package com.codefactory.reservasmscatalogservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class MessageResponseDTO {
    private String message;
    private boolean success;
    private Instant timestamp;
}