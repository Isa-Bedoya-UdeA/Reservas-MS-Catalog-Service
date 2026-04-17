package com.codefactory.reservasmscatalogservice.controller;

import com.codefactory.reservasmscatalogservice.dto.request.CreateCategoryRequestDTO;
import com.codefactory.reservasmscatalogservice.dto.request.UpdateCategoryRequestDTO;
import com.codefactory.reservasmscatalogservice.dto.response.CategoryResponseDTO;
import com.codefactory.reservasmscatalogservice.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/catalog/categories")
@RequiredArgsConstructor
@Tag(name = "Categorías", description = "Endpoints para gestión de categorías de servicios")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(
        summary = "Obtener todas las categorías",
        description = "Retorna una lista con todas las categorías activas de servicios disponibles."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de categorías retornada exitosamente")
    })
    @SecurityRequirements
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener categoría por ID",
        description = "Retorna los detalles de una categoría específica por su UUID."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categoría encontrada exitosamente", content = @Content(schema = @Schema(implementation = CategoryResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Formato de UUID inválido"),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @SecurityRequirements
    public ResponseEntity<CategoryResponseDTO> getCategoryById(
        @Parameter(description = "UUID de la categoría", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
        @PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PostMapping
    @Operation(
        summary = "Crear categoría",
        description = "Crea una nueva categoría de servicios. Requiere rol de ADMIN.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos de la categoría a crear",
            required = true,
            content = @Content(schema = @Schema(implementation = CreateCategoryRequestDTO.class))
        )
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Categoría creada exitosamente", content = @Content(schema = @Schema(implementation = CategoryResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de categoría inválidos"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "No tiene rol de ADMIN"),
        @ApiResponse(responseCode = "409", description = "El nombre de la categoría ya existe")
    })
    public ResponseEntity<CategoryResponseDTO> createCategory(
            @Valid @RequestBody CreateCategoryRequestDTO request) {
        return new ResponseEntity<>(categoryService.createCategory(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar categoría",
        description = "Actualiza los datos de una categoría existente. Requiere rol de ADMIN.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos actualizados de la categoría",
            required = true,
            content = @Content(schema = @Schema(implementation = UpdateCategoryRequestDTO.class))
        )
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categoría actualizada exitosamente", content = @Content(schema = @Schema(implementation = CategoryResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de categoría inválidos"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "No tiene rol de ADMIN"),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @Parameter(description = "UUID de la categoría", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCategoryRequestDTO request) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Desactivar categoría",
        description = "Desactiva una categoría existente (soft delete). Requiere rol de ADMIN."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Categoría desactivada exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "No tiene rol de ADMIN"),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    public ResponseEntity<Void> deactivateCategory(
        @Parameter(description = "UUID de la categoría", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
        @PathVariable UUID id) {
        categoryService.deactivateCategory(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @Operation(
        summary = "Activar categoría",
        description = "Reactiva una categoría desactivada. Requiere rol de ADMIN."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Categoría activada exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "No tiene rol de ADMIN"),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    public ResponseEntity<Void> activateCategory(
        @Parameter(description = "UUID de la categoría", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
        @PathVariable UUID id) {
        categoryService.activateCategory(id);
        return ResponseEntity.noContent().build();
    }
}
