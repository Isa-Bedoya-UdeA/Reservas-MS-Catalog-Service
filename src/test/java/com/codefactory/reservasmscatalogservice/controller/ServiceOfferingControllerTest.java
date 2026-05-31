package com.codefactory.reservasmscatalogservice.controller;

import com.codefactory.reservasmscatalogservice.dto.request.CreateServiceOfferingRequestDTO;
import com.codefactory.reservasmscatalogservice.dto.request.UpdateServiceOfferingRequestDTO;
import com.codefactory.reservasmscatalogservice.dto.response.ServiceOfferingResponseDTO;
import com.codefactory.reservasmscatalogservice.service.ServiceOfferingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ServiceOfferingController using @ExtendWith(MockitoExtension).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MS-Catalog - ServiceOfferingController (Unit)")
class ServiceOfferingControllerTest {

    @Mock
    private ServiceOfferingService serviceOfferingService;

    @InjectMocks
    private ServiceOfferingController serviceOfferingController;

    private UUID serviceId;
    private UUID providerId;
    private ServiceOfferingResponseDTO serviceResponse;

    @BeforeEach
    void setUp() {
        serviceId = UUID.randomUUID();
        providerId = UUID.randomUUID();
        
        // Set up security context
        UsernamePasswordAuthenticationToken auth = 
            new UsernamePasswordAuthenticationToken(providerId.toString(), null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        serviceResponse = ServiceOfferingResponseDTO.builder()
                .idServicio(serviceId)
                .idProveedor(providerId)
                .nombreServicio("Corte de cabello")
                .duracionMinutos(30)
                .precio(new BigDecimal("25.00"))
                .descripcion("Corte básico")
                .activo(true)
                .capacidadMaxima(5)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("createServiceOffering returns CREATED status")
    void createServiceOffering_ReturnsCreated() {
        CreateServiceOfferingRequestDTO request = CreateServiceOfferingRequestDTO.builder()
                .nombreServicio("Nuevo Servicio")
                .duracionMinutos(45)
                .precio(new BigDecimal("30.00"))
                .capacidadMaxima(1)
                .build();

        when(serviceOfferingService.createServiceOffering(any(), eq(providerId)))
                .thenReturn(serviceResponse);

        ResponseEntity<EntityModel<ServiceOfferingResponseDTO>> response = 
            serviceOfferingController.createServiceOffering(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("updateServiceOffering returns updated service")
    void updateServiceOffering_ReturnsUpdated() {
        UpdateServiceOfferingRequestDTO request = UpdateServiceOfferingRequestDTO.builder()
                .nombreServicio("Servicio Actualizado")
                .duracionMinutos(60)
                .precio(new BigDecimal("40.00"))
                .build();

        ServiceOfferingResponseDTO updated = ServiceOfferingResponseDTO.builder()
                .idServicio(serviceId)
                .idProveedor(providerId)
                .nombreServicio("Servicio Actualizado")
                .duracionMinutos(60)
                .precio(new BigDecimal("40.00"))
                .activo(true)
                .build();

        when(serviceOfferingService.updateServiceOffering(eq(serviceId), any(), eq(providerId)))
                .thenReturn(updated);

        ResponseEntity<EntityModel<ServiceOfferingResponseDTO>> response = 
            serviceOfferingController.updateServiceOffering(serviceId, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).isNotNull();
        assertThat(response.getBody().getContent().getNombreServicio()).isEqualTo("Servicio Actualizado");
    }

    @Test
    @DisplayName("getServiceById returns service when found")
    void getServiceById_ReturnsService() {
        when(serviceOfferingService.getServiceById(serviceId)).thenReturn(serviceResponse);

        ResponseEntity<EntityModel<ServiceOfferingResponseDTO>> response = 
            serviceOfferingController.getServiceById(serviceId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("getAllActiveServices returns list of active services")
    void getAllActiveServices_ReturnsList() {
        when(serviceOfferingService.getAllActiveServices()).thenReturn(List.of(serviceResponse));

        ResponseEntity<CollectionModel<EntityModel<ServiceOfferingResponseDTO>>> response = 
            serviceOfferingController.getAllActiveServices();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).hasSize(1);
    }

    @Test
    @DisplayName("deleteServiceOffering returns NO_CONTENT")
    void deleteServiceOffering_ReturnsNoContent() {
        doNothing().when(serviceOfferingService).deleteServiceOffering(serviceId, providerId);

        ResponseEntity<Void> response = serviceOfferingController.deleteServiceOffering(serviceId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(serviceOfferingService).deleteServiceOffering(serviceId, providerId);
    }

    @Test
    @DisplayName("permanentlyDeleteServiceOffering returns NO_CONTENT")
    void permanentlyDeleteServiceOffering_ReturnsNoContent() {
        doNothing().when(serviceOfferingService).permanentlyDeleteServiceOffering(serviceId);

        ResponseEntity<Void> response = 
            serviceOfferingController.permanentlyDeleteServiceOffering(serviceId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(serviceOfferingService).permanentlyDeleteServiceOffering(serviceId);
    }

    @Test
    @DisplayName("getServicesByProvider returns provider's services")
    void getServicesByProvider_ReturnsList() {
        when(serviceOfferingService.getServicesByProvider(providerId, providerId))
                .thenReturn(List.of(serviceResponse));

        ResponseEntity<CollectionModel<EntityModel<ServiceOfferingResponseDTO>>> response = 
            serviceOfferingController.getServicesByProvider();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).hasSize(1);
    }
}