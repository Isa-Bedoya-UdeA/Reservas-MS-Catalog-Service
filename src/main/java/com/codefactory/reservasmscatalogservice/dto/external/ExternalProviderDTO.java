package com.codefactory.reservasmscatalogservice.dto.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalProviderDTO {
    private Long id;
    private String nombreComercial;
    private String email;
    private String telefonoContacto;
    private Long idCategoria;
    private String direccion;
}