package com.codefactory.reservasmscatalogservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDTO {
    private UUID idCategoria;
    private String nombreCategoria;
    private String descripcion;
    private Boolean activa;
    private LocalDateTime createdAt;
}
