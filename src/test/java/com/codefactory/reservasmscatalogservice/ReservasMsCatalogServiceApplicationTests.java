package com.codefactory.reservasmscatalogservice;

import com.codefactory.reservasmscatalogservice.controller.HealthController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ReservasMsCatalogServiceApplicationTests {

    @Autowired
    private HealthController healthController;

    /**
     * Test básico: verifica que el contexto de Spring se carga correctamente.
     * Este test es generado automáticamente por Spring Initializr.
     */
    @Test
    void contextLoads() {
        assertNotNull(healthController, "HealthController debe estar inyectado");
    }

    /**
     * Test del endpoint GET /api/ (health check)
     * Verifica que retorna status "UP" y timestamp válido.
     */
    @Test
    void health_endpoint_returnsUpStatus() {
        // Given: No se requiere configuración previa

        // When: Se invoca el método health del controller
        ResponseEntity<Map<String, Object>> response = healthController.health();

        // Then: Se validan las respuestas esperadas
        assertEquals(200, response.getStatusCodeValue(),
                "El status HTTP debe ser 200 OK");

        assertNotNull(response.getBody(),
                "El cuerpo de la respuesta no debe ser nulo");

        assertEquals("UP", response.getBody().get("status"),
                "El status de salud debe ser 'UP'");

        assertNotNull(response.getBody().get("timestamp"),
                "El timestamp debe estar presente en la respuesta");

        // Verificación adicional: el timestamp debe ser un Instant válido
        assertTrue(response.getBody().get("timestamp") instanceof java.time.Instant,
                "El timestamp debe ser de tipo Instant");
    }

    /**
     * Test del endpoint GET /api/version
     * Verifica que retorna la versión y nombre del servicio correctos.
     */
    @Test
    void version_endpoint_returnsCorrectVersion() {
        // Given: No se requiere configuración previa

        // When: Se invoca el método version del controller
        ResponseEntity<Map<String, String>> response = healthController.version();

        // Then: Se validan las respuestas esperadas
        assertEquals(200, response.getStatusCodeValue(),
                "El status HTTP debe ser 200 OK");

        assertNotNull(response.getBody(),
                "El cuerpo de la respuesta no debe ser nulo");

        assertEquals("1.0.0-SNAPSHOT", response.getBody().get("version"),
                "La versión debe ser '1.0.0-SNAPSHOT'");

        assertEquals("Reservas-MS-Catalog-Service", response.getBody().get("service"),
                "El nombre del servicio debe coincidir con la configuración");
    }

    /**
     * Test de performance básico para el endpoint health
     * Verifica que la respuesta sea rápida (< 500ms en entorno local)
     */
    @Test
    void health_endpoint_performance_acceptable() {
        long startTime = System.currentTimeMillis();

        healthController.health();

        long executionTime = System.currentTimeMillis() - startTime;

        // Nota: 500ms es un límite conservador para desarrollo local
        // En producción con contenedores, podría ajustarse a 200ms
        assertTrue(executionTime < 500,
                String.format("El endpoint health tardó %dms, esperado < 500ms", executionTime));
    }

}
