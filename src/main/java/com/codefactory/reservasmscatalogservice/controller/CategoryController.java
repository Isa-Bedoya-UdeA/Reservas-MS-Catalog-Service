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
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/catalog/categories")
@RequiredArgsConstructor
@Tag(name = "Categorías", description = "Endpoints para gestión de categorías de servicios")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(
        summary = "Obtener todas las categorías",
        description = "Retorna una lista con todas las categorías de servicios (incluyendo inactivas)."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de categorías retornada exitosamente")
    })
    @SecurityRequirements
    public ResponseEntity<CollectionModel<EntityModel<CategoryResponseDTO>>> getAllCategories() {
        List<CategoryResponseDTO> categories = categoryService.getAllCategories();
        List<EntityModel<CategoryResponseDTO>> models = categories.stream()
            .map(c -> EntityModel.of(c,
                linkTo(methodOn(CategoryController.class).getCategoryById(c.getIdCategoria())).withSelfRel()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(models,
            linkTo(methodOn(CategoryController.class).getAllCategories()).withSelfRel()));
    }

    @GetMapping("/active")
    @Operation(
        summary = "Obtener categorías activas",
        description = "Retorna una lista con solo las categorías activas de servicios disponibles. Usado por proveedores para seleccionar categorías al crear servicios."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de categorías activas retornada exitosamente")
    })
    @SecurityRequirements
    public ResponseEntity<CollectionModel<EntityModel<CategoryResponseDTO>>> getActiveCategories() {
        List<CategoryResponseDTO> categories = categoryService.getActiveCategories();
        List<EntityModel<CategoryResponseDTO>> models = categories.stream()
            .map(c -> EntityModel.of(c,
                linkTo(methodOn(CategoryController.class).getCategoryById(c.getIdCategoria())).withSelfRel()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(models,
            linkTo(methodOn(CategoryController.class).getActiveCategories()).withSelfRel()));
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
    public ResponseEntity<EntityModel<CategoryResponseDTO>> getCategoryById(
        @Parameter(description = "UUID de la categoría", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
        @PathVariable UUID id) {
        CategoryResponseDTO category = categoryService.getCategoryById(id);
        EntityModel<CategoryResponseDTO> entityModel = EntityModel.of(category,
            linkTo(methodOn(CategoryController.class).getCategoryById(id)).withSelfRel(),
            linkTo(methodOn(CategoryController.class).getActiveCategories()).withRel("active-categories"),
            linkTo(methodOn(CategoryController.class).getAllCategories()).withRel("all-categories"));
        return ResponseEntity.ok(entityModel);
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
    public ResponseEntity<EntityModel<CategoryResponseDTO>> createCategory(
            @Valid @RequestBody CreateCategoryRequestDTO request) {
        CategoryResponseDTO created = categoryService.createCategory(request);
        EntityModel<CategoryResponseDTO> entityModel = EntityModel.of(created,
            linkTo(methodOn(CategoryController.class).getCategoryById(created.getIdCategoria())).withSelfRel(),
            linkTo(methodOn(CategoryController.class).getAllCategories()).withRel("all-categories"));
        return new ResponseEntity<>(entityModel, HttpStatus.CREATED);
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
    public ResponseEntity<EntityModel<CategoryResponseDTO>> updateCategory(
            @Parameter(description = "UUID de la categoría", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCategoryRequestDTO request) {
        CategoryResponseDTO updated = categoryService.updateCategory(id, request);
        EntityModel<CategoryResponseDTO> entityModel = EntityModel.of(updated,
            linkTo(methodOn(CategoryController.class).getCategoryById(id)).withSelfRel(),
            linkTo(methodOn(CategoryController.class).getAllCategories()).withRel("all-categories"));
        return ResponseEntity.ok(entityModel);
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