package com.codefactory.reservasmscatalogservice.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCategoryRequestDTO {

    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    private String nombreCategoria;

    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    private String descripcion;

    private Boolean activa;
}
