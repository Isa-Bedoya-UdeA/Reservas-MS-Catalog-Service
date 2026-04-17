package com.codefactory.reservasmscatalogservice.controller;

import com.codefactory.reservasmscatalogservice.dto.request.CreateServiceOfferingRequestDTO;
import com.codefactory.reservasmscatalogservice.dto.response.ServiceOfferingResponseDTO;
import com.codefactory.reservasmscatalogservice.service.impl.ServiceOfferingServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/catalog/services")
@RequiredArgsConstructor
@Tag(name = "Servicios", description = "Endpoints para gestión de servicios ofertados")
public class ServiceOfferingController {

    private final ServiceOfferingServiceImpl serviceOfferingService;

    @PostMapping
    @Operation(
        summary = "Crear servicio ofertado",
        description = "Crea un nuevo servicio ofertado por un proveedor. Requiere rol de PROVEEDOR.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del servicio a crear",
            required = true,
            content = @Content(schema = @Schema(implementation = CreateServiceOfferingRequestDTO.class))
        )
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Servicio creado exitosamente", content = @Content(schema = @Schema(implementation = ServiceOfferingResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de servicio inválidos"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "No tiene rol de PROVEEDOR"),
        @ApiResponse(responseCode = "404", description = "Proveedor no encontrado"),
        @ApiResponse(responseCode = "409", description = "El servicio ya existe")
    })
    public ResponseEntity<ServiceOfferingResponseDTO> createServiceOffering(
            @Valid @RequestBody CreateServiceOfferingRequestDTO request,
            @Parameter(description = "UUID del proveedor", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @RequestParam UUID idProveedor) {
        return new ResponseEntity<>(serviceOfferingService.createServiceOffering(request, idProveedor),
                HttpStatus.CREATED);
    }
}
