package com.codefactory.reservasmscatalogservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Health Check", description = "Endpoints para verificación de estado del servicio")
public class HealthController {

    @GetMapping("/")
    @Operation(
        summary = "Health check",
        description = "Verifica si el servicio está funcionando correctamente. Retorna el estado y timestamp actual."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Servicio funcionando correctamente")
    })
    @SecurityRequirements
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "timestamp", Instant.now()
        ));
    }

    @GetMapping("/version")
    @Operation(
        summary = "Versión del servicio",
        description = "Retorna la versión del servicio y el nombre del microservicio."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Información de versión retornada exitosamente")
    })
    @SecurityRequirements
    public ResponseEntity<Map<String, String>> version() {
        return ResponseEntity.ok(Map.of(
                "version", "1.0.0-SNAPSHOT",
                "service", "Reservas-MS-Catalog-Service"
        ));
    }
}