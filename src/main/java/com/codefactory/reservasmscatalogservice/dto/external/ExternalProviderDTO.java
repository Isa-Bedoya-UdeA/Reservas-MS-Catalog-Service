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
public class ExternalProviderDTO {
    private String nombreComercial;
    private String email;
    private String telefonoContacto;
    private UUID idCategoria;
    private String direccion;
}