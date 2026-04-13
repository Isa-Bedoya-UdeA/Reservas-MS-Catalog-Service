package com.codefactory.reservasmscatalogservice.dto.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalClientDTO {
    private String nombre;
    private String email;
    private String telefono;
    private boolean emailVerificado;
}