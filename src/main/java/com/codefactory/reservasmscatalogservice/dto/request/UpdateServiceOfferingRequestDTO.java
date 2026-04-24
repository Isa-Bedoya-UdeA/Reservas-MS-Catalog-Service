package com.codefactory.reservasmscatalogservice.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateServiceOfferingRequestDTO {

    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    private String nombreServicio;

    @Min(value = 1, message = "La duración debe ser mayor a 0")
    private Integer duracionMinutos;

    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    private BigDecimal precio;

    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    private String descripcion;

    @Min(value = 0, message = "La capacidad máxima debe ser al menos 0 (0 = sin límite)")
    private Integer capacidadMaxima;
}
