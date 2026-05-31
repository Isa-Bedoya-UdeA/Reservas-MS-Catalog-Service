package com.codefactory.reservasmscatalogservice.dto.response;

import com.codefactory.reservasmscatalogservice.dto.external.ExternalProviderDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOfferingResponseDTO extends RepresentationModel<ServiceOfferingResponseDTO> {
    private UUID idServicio;
    private UUID idProveedor;
    private String nombreServicio;
    private Integer duracionMinutos;
    private BigDecimal precio;
    private String descripcion;
    private Boolean activo;
    private Integer capacidadMaxima;
    private ExternalProviderDTO proveedorInfo;
    private LocalDateTime createdAt;
}