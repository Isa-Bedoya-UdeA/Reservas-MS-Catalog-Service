package com.codefactory.reservasmscatalogservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class VersionResponseDTO {
    private String version;
    private String serviceName;
    private Instant timestamp;
}