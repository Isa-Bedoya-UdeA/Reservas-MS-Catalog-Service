package com.codefactory.reservasmscatalogservice.controller;

import com.codefactory.reservasmscatalogservice.dto.request.CreateServiceOfferingRequestDTO;
import com.codefactory.reservasmscatalogservice.dto.request.UpdateServiceOfferingRequestDTO;
import com.codefactory.reservasmscatalogservice.dto.response.ServiceOfferingResponseDTO;
import com.codefactory.reservasmscatalogservice.service.ServiceOfferingService;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/catalog/services")
@RequiredArgsConstructor
@Tag(name = "Servicios", description = "Endpoints para gestión de servicios ofertados")
public class ServiceOfferingController {

    private final ServiceOfferingService serviceOfferingService;

    @PostMapping
    @Operation(
        summary = "Crear servicio",
        description = "Crea un nuevo servicio ofertado por el proveedor autenticado. Requiere rol de PROVEEDOR."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Servicio creado exitosamente", content = @Content(schema = @Schema(implementation = ServiceOfferingResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de servicio inválidos"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "No tiene rol de PROVEEDOR"),
        @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
    })
    public ResponseEntity<EntityModel<ServiceOfferingResponseDTO>> createServiceOffering(
            @Valid @RequestBody CreateServiceOfferingRequestDTO request) {
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        UUID idProveedor = UUID.fromString(userIdStr);
        ServiceOfferingResponseDTO created = serviceOfferingService.createServiceOffering(request, idProveedor);
        EntityModel<ServiceOfferingResponseDTO> entityModel = EntityModel.of(created,
            linkTo(methodOn(ServiceOfferingController.class).getServiceById(created.getIdServicio())).withSelfRel(),
            linkTo(methodOn(ServiceOfferingController.class).getAllActiveServices()).withRel("active-services"));
        return new ResponseEntity<>(entityModel, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar servicio",
        description = "Actualiza un servicio existente. Solo el proveedor creador puede modificarlo. Requiere rol de PROVEEDOR."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Servicio actualizado exitosamente", content = @Content(schema = @Schema(implementation = ServiceOfferingResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de servicio inválidos"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "No es el creador del servicio"),
        @ApiResponse(responseCode = "404", description = "Servicio no encontrado")
    })
    public ResponseEntity<EntityModel<ServiceOfferingResponseDTO>> updateServiceOffering(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateServiceOfferingRequestDTO request) {
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        UUID idProveedor = UUID.fromString(userIdStr);
        ServiceOfferingResponseDTO updated = serviceOfferingService.updateServiceOffering(id, request, idProveedor);
        EntityModel<ServiceOfferingResponseDTO> entityModel = EntityModel.of(updated,
            linkTo(methodOn(ServiceOfferingController.class).getServiceById(id)).withSelfRel(),
            linkTo(methodOn(ServiceOfferingController.class).getAllActiveServices()).withRel("active-services"));
        return ResponseEntity.ok(entityModel);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Desactivar servicio (Soft Delete)",
        description = "Desactiva un servicio (cambia estado a INACTIVO). Solo el proveedor creador puede desactivarlo. Requiere rol de PROVEEDOR."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Servicio desactivado exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "No es el creador del servicio"),
        @ApiResponse(responseCode = "404", description = "Servicio no encontrado"),
        @ApiResponse(responseCode = "409", description = "El servicio ya está inactivo")
    })
    public ResponseEntity<Void> deleteServiceOffering(@PathVariable UUID id) {
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        UUID idProveedor = UUID.fromString(userIdStr);
        serviceOfferingService.deleteServiceOffering(id, idProveedor);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/disable")
    @Operation(
        summary = "Desactivar servicio (alternativo)",
        description = "Desactiva un servicio (cambia estado a INACTIVO). Solo el proveedor creador puede desactivarlo. Requiere rol de PROVEEDOR."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Servicio desactivado exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "No es el creador del servicio"),
        @ApiResponse(responseCode = "404", description = "Servicio no encontrado"),
        @ApiResponse(responseCode = "409", description = "El servicio ya está inactivo")
    })
    public ResponseEntity<Void> disableServiceOffering(@PathVariable UUID id) {
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        UUID idProveedor = UUID.fromString(userIdStr);
        serviceOfferingService.deleteServiceOffering(id, idProveedor);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/permanent")
    @Operation(
        summary = "Eliminar servicio permanentemente",
        description = "Elimina un servicio permanentemente de la base de datos. Solo administradores. Requiere rol de ADMIN."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Servicio eliminado permanentemente"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "No tiene rol de ADMIN"),
        @ApiResponse(responseCode = "404", description = "Servicio no encontrado")
    })
    public ResponseEntity<Void> permanentlyDeleteServiceOffering(@PathVariable UUID id) {
        serviceOfferingService.permanentlyDeleteServiceOffering(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener servicio por ID",
        description = "Retorna un servicio específico por su ID. Público para clientes."
    )
    @SecurityRequirements
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Servicio encontrado", content = @Content(schema = @Schema(implementation = ServiceOfferingResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Servicio no encontrado")
    })
    public ResponseEntity<EntityModel<ServiceOfferingResponseDTO>> getServiceById(@PathVariable UUID id) {
        ServiceOfferingResponseDTO service = serviceOfferingService.getServiceById(id);
        EntityModel<ServiceOfferingResponseDTO> entityModel = EntityModel.of(service,
            linkTo(methodOn(ServiceOfferingController.class).getServiceById(id)).withSelfRel(),
            linkTo(methodOn(ServiceOfferingController.class).getAllActiveServices()).withRel("active-services"),
            linkTo(methodOn(ServiceOfferingController.class).getActiveServicesByProvider(service.getIdProveedor())).withRel("provider-services"));
        return ResponseEntity.ok(entityModel);
    }

    @GetMapping("/provider")
    @Operation(
        summary = "Listar servicios del proveedor",
        description = "Retorna todos los servicios (activos e inactivos) del proveedor autenticado. Requiere rol de PROVEEDOR."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de servicios retornada exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "No tiene rol de PROVEEDOR")
    })
    public ResponseEntity<CollectionModel<EntityModel<ServiceOfferingResponseDTO>>> getServicesByProvider() {
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        UUID idProveedor = UUID.fromString(userIdStr);
        List<ServiceOfferingResponseDTO> services = serviceOfferingService.getServicesByProvider(idProveedor, idProveedor);
        List<EntityModel<ServiceOfferingResponseDTO>> models = services.stream()
            .map(s -> EntityModel.of(s,
                linkTo(methodOn(ServiceOfferingController.class).getServiceById(s.getIdServicio())).withSelfRel()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(models,
            linkTo(methodOn(ServiceOfferingController.class).getServicesByProvider()).withSelfRel()));
    }

    @GetMapping("/active")
    @Operation(
        summary = "Listar servicios activos",
        description = "Retorna todos los servicios activos de todas las categorías activas. Público para clientes."
    )
    @SecurityRequirements
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de servicios activos retornada exitosamente")
    })
    public ResponseEntity<CollectionModel<EntityModel<ServiceOfferingResponseDTO>>> getAllActiveServices() {
        List<ServiceOfferingResponseDTO> services = serviceOfferingService.getAllActiveServices();
        List<EntityModel<ServiceOfferingResponseDTO>> models = services.stream()
            .map(s -> EntityModel.of(s,
                linkTo(methodOn(ServiceOfferingController.class).getServiceById(s.getIdServicio())).withSelfRel()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(models,
            linkTo(methodOn(ServiceOfferingController.class).getAllActiveServices()).withSelfRel()));
    }

    @GetMapping("/active/category/{idCategoria}")
    @Operation(
        summary = "Listar servicios activos por categoría",
        description = "Retorna todos los servicios activos de una categoría específica. Público para clientes."
    )
    @SecurityRequirements
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de servicios activos retornada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada o inactiva")
    })
    public ResponseEntity<CollectionModel<EntityModel<ServiceOfferingResponseDTO>>> getActiveServicesByCategory(
            @PathVariable UUID idCategoria) {
        List<ServiceOfferingResponseDTO> services = serviceOfferingService.getActiveServicesByCategory(idCategoria);
        List<EntityModel<ServiceOfferingResponseDTO>> models = services.stream()
            .map(s -> EntityModel.of(s,
                linkTo(methodOn(ServiceOfferingController.class).getServiceById(s.getIdServicio())).withSelfRel()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(models,
            linkTo(methodOn(ServiceOfferingController.class).getActiveServicesByCategory(idCategoria)).withSelfRel()));
    }

    @GetMapping("/active/provider/{idProveedor}")
    @Operation(
        summary = "Listar servicios activos por proveedor",
        description = "Retorna todos los servicios activos de un proveedor específico. Público para clientes."
    )
    @SecurityRequirements
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de servicios activos retornada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
    })
    public ResponseEntity<CollectionModel<EntityModel<ServiceOfferingResponseDTO>>> getActiveServicesByProvider(
            @PathVariable UUID idProveedor) {
        List<ServiceOfferingResponseDTO> services = serviceOfferingService.getActiveServicesByProvider(idProveedor);
        List<EntityModel<ServiceOfferingResponseDTO>> models = services.stream()
            .map(s -> EntityModel.of(s,
                linkTo(methodOn(ServiceOfferingController.class).getServiceById(s.getIdServicio())).withSelfRel()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(models,
            linkTo(methodOn(ServiceOfferingController.class).getActiveServicesByProvider(idProveedor)).withSelfRel()));
    }
}