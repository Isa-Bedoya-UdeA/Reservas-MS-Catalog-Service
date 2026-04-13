package com.codefactory.reservasmscatalogservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.codefactory.reservasmscatalogservice.dto.external.ExternalProviderDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOfferingResponseDTO {
    private UUID idServicio;
    private UUID idProveedor;
    private String nombreServicio;
    private Integer duracionMinutos;
    private BigDecimal precio;
    private String descripcion;
    private Boolean activo;
    private ExternalProviderDTO proveedorInfo;
    private LocalDateTime createdAt;
}
