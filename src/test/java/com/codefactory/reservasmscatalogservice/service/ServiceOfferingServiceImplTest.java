package com.codefactory.reservasmscatalogservice.service;

import com.codefactory.reservasmscatalogservice.client.AuthClient;
import com.codefactory.reservasmscatalogservice.dto.request.CreateServiceOfferingRequestDTO;
import com.codefactory.reservasmscatalogservice.dto.request.UpdateServiceOfferingRequestDTO;
import com.codefactory.reservasmscatalogservice.dto.response.CategoryResponseDTO;
import com.codefactory.reservasmscatalogservice.dto.response.ServiceOfferingResponseDTO;
import com.codefactory.reservasmscatalogservice.entity.ServiceOffering;
import com.codefactory.reservasmscatalogservice.exception.ProviderMismatchException;
import com.codefactory.reservasmscatalogservice.exception.ServiceAlreadyInactiveException;
import com.codefactory.reservasmscatalogservice.exception.ServiceNotFoundException;
import com.codefactory.reservasmscatalogservice.mapper.ServiceOfferingMapper;
import com.codefactory.reservasmscatalogservice.repository.ServiceOfferingRepository;
import com.codefactory.reservasmscatalogservice.service.impl.ServiceOfferingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias - MS-CATALOG-SERVICE
 * Sprint 2 - ServiceOfferingServiceImpl
 *
 * HU08: Crear servicio
 * HU09: Editar servicio
 * HU10: Inhabilitar servicio (Soft Delete)
 * HU11: Listar servicios (Proveedor)
 * HU12: Listar servicios disponibles (Cliente)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MS-Catalog - ServiceOfferingServiceImpl")
class ServiceOfferingServiceImplTest {

    @Mock private ServiceOfferingRepository serviceOfferingRepository;
    @Mock private ServiceOfferingMapper serviceOfferingMapper;
    @Mock private CategoryService categoryService;
    @Mock private AuthClient authClient;

    @InjectMocks
    private ServiceOfferingServiceImpl serviceOfferingService;

    private UUID serviceId;
    private UUID proveedorId;
    private ServiceOffering serviceEntity;
    private ServiceOfferingResponseDTO serviceResponse;

    @BeforeEach
    void setUp() {
        serviceId = UUID.randomUUID();
        proveedorId = UUID.randomUUID();

        serviceEntity = ServiceOffering.builder()
                .idServicio(serviceId)
                .idProveedor(proveedorId)
                .nombreServicio("Corte de Cabello")
                .duracionMinutos(30)
                .precio(new BigDecimal("25000.00"))
                .activo(true)
                .capacidadMaxima(1)
                .build();

        serviceResponse = ServiceOfferingResponseDTO.builder()
                .idServicio(serviceId)
                .idProveedor(proveedorId)
                .nombreServicio("Corte de Cabello")
                .duracionMinutos(30)
                .precio(new BigDecimal("25000.00"))
                .activo(true)
                .build();
    }

    // =========================================================================
    // HU08 - Crear servicio
    // =========================================================================

    @Test
    @DisplayName("HU08-CP-001: Crear servicio con proveedor válido → retorna DTO con activo=true")
    void createServiceOffering_ProveedorValido_RetornaServicioDTO() {
        // Arrange
        CreateServiceOfferingRequestDTO request = CreateServiceOfferingRequestDTO.builder()
                .nombreServicio("Corte de Cabello")
                .duracionMinutos(30)
                .precio(new BigDecimal("25000.00"))
                .capacidadMaxima(1)
                .build();

        when(authClient.getProviderById(proveedorId)).thenReturn(ResponseEntity.ok(null));
        when(serviceOfferingMapper.toEntity(request)).thenReturn(serviceEntity);
        when(serviceOfferingRepository.save(any())).thenReturn(serviceEntity);
        when(serviceOfferingMapper.toDto(serviceEntity)).thenReturn(serviceResponse);

        // Act
        ServiceOfferingResponseDTO resultado = serviceOfferingService.createServiceOffering(request, proveedorId);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombreServicio()).isEqualTo("Corte de Cabello");
        assertThat(resultado.getActivo()).isTrue();
        assertThat(resultado.getIdProveedor()).isEqualTo(proveedorId);
        verify(serviceOfferingRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("HU08-CP-002: Crear servicio con proveedor inexistente → lanza ServiceNotFoundException")
    void createServiceOffering_ProveedorInexistente_LanzaServiceNotFoundException() {
        // Arrange
        CreateServiceOfferingRequestDTO request = CreateServiceOfferingRequestDTO.builder()
                .nombreServicio("Corte de Cabello")
                .duracionMinutos(30)
                .precio(new BigDecimal("25000.00"))
                .capacidadMaxima(1)
                .build();

        when(authClient.getProviderById(proveedorId)).thenThrow(new RuntimeException("Feign error"));

        // Act & Assert
        assertThatThrownBy(() -> serviceOfferingService.createServiceOffering(request, proveedorId))
                .isInstanceOf(ServiceNotFoundException.class)
                .hasMessageContaining("Proveedor no encontrado");

        verify(serviceOfferingRepository, never()).save(any());
    }

    @Test
    @DisplayName("HU08-CP-003: Al crear, el servicio queda automáticamente activo=true")
    void createServiceOffering_ServicioQuedaActivoPorDefecto() {
        // Arrange
        CreateServiceOfferingRequestDTO request = CreateServiceOfferingRequestDTO.builder()
                .nombreServicio("Manicure")
                .duracionMinutos(45)
                .precio(new BigDecimal("30000.00"))
                .capacidadMaxima(1)
                .build();

        when(authClient.getProviderById(proveedorId)).thenReturn(ResponseEntity.ok(null));
        when(serviceOfferingMapper.toEntity(request)).thenReturn(serviceEntity);
        when(serviceOfferingRepository.save(any())).thenReturn(serviceEntity);
        when(serviceOfferingMapper.toDto(serviceEntity)).thenReturn(serviceResponse);

        // Act
        serviceOfferingService.createServiceOffering(request, proveedorId);

        // Assert - verificar que se setea activo=true antes de guardar
        verify(serviceOfferingRepository).save(argThat(s -> Boolean.TRUE.equals(s.getActivo())));
    }

    // =========================================================================
    // HU09 - Editar servicio
    // =========================================================================

    @Test
    @DisplayName("HU09-CP-001: Editar servicio propio con datos válidos → retorna DTO actualizado")
    void updateServiceOffering_OwnerValido_RetornaDTOActualizado() {
        // Arrange
        UpdateServiceOfferingRequestDTO request = UpdateServiceOfferingRequestDTO.builder()
                .nombreServicio("Corte Premium")
                .precio(new BigDecimal("35000.00"))
                .build();

        ServiceOfferingResponseDTO respuestaActualizada = ServiceOfferingResponseDTO.builder()
                .idServicio(serviceId)
                .nombreServicio("Corte Premium")
                .precio(new BigDecimal("35000.00"))
                .activo(true)
                .build();

        when(serviceOfferingRepository.findById(serviceId)).thenReturn(Optional.of(serviceEntity));
        when(serviceOfferingRepository.save(any())).thenReturn(serviceEntity);
        when(serviceOfferingMapper.toDto(any())).thenReturn(respuestaActualizada);

        // Act
        ServiceOfferingResponseDTO resultado = serviceOfferingService.updateServiceOffering(serviceId, request, proveedorId);

        // Assert
        assertThat(resultado.getNombreServicio()).isEqualTo("Corte Premium");
        verify(serviceOfferingRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("HU09-CP-002: Editar servicio de otro proveedor → lanza ProviderMismatchException")
    void updateServiceOffering_OtroProveedor_LanzaProviderMismatchException() {
        // Arrange
        UUID otroProveedorId = UUID.randomUUID();
        when(serviceOfferingRepository.findById(serviceId)).thenReturn(Optional.of(serviceEntity));

        // Act & Assert
        assertThatThrownBy(() -> serviceOfferingService.updateServiceOffering(serviceId, new UpdateServiceOfferingRequestDTO(), otroProveedorId))
                .isInstanceOf(ProviderMismatchException.class)
                .hasMessageContaining("No tiene permiso");

        verify(serviceOfferingRepository, never()).save(any());
    }

    @Test
    @DisplayName("HU09-CP-003: Editar servicio inexistente → lanza ServiceNotFoundException")
    void updateServiceOffering_IdInexistente_LanzaServiceNotFoundException() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();
        when(serviceOfferingRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> serviceOfferingService.updateServiceOffering(idInexistente, new UpdateServiceOfferingRequestDTO(), proveedorId))
                .isInstanceOf(ServiceNotFoundException.class);
    }

    @Test
    @DisplayName("HU09-CP-004: Editar con campos null → no modifica esos campos")
    void updateServiceOffering_CamposNull_NoModificaCampos() {
        // Arrange - request con todos los campos null
        UpdateServiceOfferingRequestDTO request = new UpdateServiceOfferingRequestDTO();
        String nombreOriginal = serviceEntity.getNombreServicio();

        when(serviceOfferingRepository.findById(serviceId)).thenReturn(Optional.of(serviceEntity));
        when(serviceOfferingRepository.save(any())).thenReturn(serviceEntity);
        when(serviceOfferingMapper.toDto(any())).thenReturn(serviceResponse);

        // Act
        serviceOfferingService.updateServiceOffering(serviceId, request, proveedorId);

        // Assert - el nombre no debió cambiar
        assertThat(serviceEntity.getNombreServicio()).isEqualTo(nombreOriginal);
    }

    // =========================================================================
    // HU10 - Inhabilitar servicio (Soft Delete)
    // =========================================================================

    @Test
    @DisplayName("HU10-CP-001: Inhabilitar servicio activo propio → queda activo=false")
    void deleteServiceOffering_ServicioActivo_DesactivaYGuarda() {
        // Arrange
        when(serviceOfferingRepository.findById(serviceId)).thenReturn(Optional.of(serviceEntity));
        when(serviceOfferingRepository.save(any())).thenReturn(serviceEntity);

        // Act
        serviceOfferingService.deleteServiceOffering(serviceId, proveedorId);

        // Assert
        assertThat(serviceEntity.getActivo()).isFalse();
        verify(serviceOfferingRepository, times(1)).save(serviceEntity);
    }

    @Test
    @DisplayName("HU10-CP-002: Inhabilitar servicio ya inactivo → lanza ServiceAlreadyInactiveException")
    void deleteServiceOffering_ServicioYaInactivo_LanzaServiceAlreadyInactiveException() {
        // Arrange
        serviceEntity.setActivo(false);
        when(serviceOfferingRepository.findById(serviceId)).thenReturn(Optional.of(serviceEntity));

        // Act & Assert
        assertThatThrownBy(() -> serviceOfferingService.deleteServiceOffering(serviceId, proveedorId))
                .isInstanceOf(ServiceAlreadyInactiveException.class)
                .hasMessageContaining("ya está inactivo");

        verify(serviceOfferingRepository, never()).save(any());
    }

    @Test
    @DisplayName("HU10-CP-003: Inhabilitar servicio de otro proveedor → lanza ProviderMismatchException")
    void deleteServiceOffering_OtroProveedor_LanzaProviderMismatchException() {
        // Arrange
        UUID otroProveedorId = UUID.randomUUID();
        when(serviceOfferingRepository.findById(serviceId)).thenReturn(Optional.of(serviceEntity));

        // Act & Assert
        assertThatThrownBy(() -> serviceOfferingService.deleteServiceOffering(serviceId, otroProveedorId))
                .isInstanceOf(ProviderMismatchException.class);

        verify(serviceOfferingRepository, never()).save(any());
    }

    @Test
    @DisplayName("HU10-CP-004: Inhabilitar servicio inexistente → lanza ServiceNotFoundException")
    void deleteServiceOffering_IdInexistente_LanzaServiceNotFoundException() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();
        when(serviceOfferingRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> serviceOfferingService.deleteServiceOffering(idInexistente, proveedorId))
                .isInstanceOf(ServiceNotFoundException.class);
    }

    // =========================================================================
    // HU11 - Listar servicios del proveedor
    // =========================================================================

    @Test
    @DisplayName("HU11-CP-001: Listar servicios del proveedor autenticado → retorna lista propia")
    void getServicesByProvider_ProveedorAutenticado_RetornaListaPropia() {
        // Arrange
        when(serviceOfferingRepository.findByIdProveedor(proveedorId)).thenReturn(List.of(serviceEntity));
        when(serviceOfferingMapper.toDto(serviceEntity)).thenReturn(serviceResponse);

        // Act
        List<ServiceOfferingResponseDTO> resultado = serviceOfferingService.getServicesByProvider(proveedorId, proveedorId);

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getIdProveedor()).isEqualTo(proveedorId);
    }

    @Test
    @DisplayName("HU11-CP-002: Listar servicios de otro proveedor → lanza ProviderMismatchException")
    void getServicesByProvider_OtroProveedor_LanzaProviderMismatchException() {
        // Arrange
        UUID otroProveedorId = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> serviceOfferingService.getServicesByProvider(proveedorId, otroProveedorId))
                .isInstanceOf(ProviderMismatchException.class);

        verify(serviceOfferingRepository, never()).findByIdProveedor(any());
    }

    // =========================================================================
    // HU12 - Listar servicios disponibles (Cliente)
    // =========================================================================

    @Test
    @DisplayName("HU12-CP-001: Listar todos los servicios activos → retorna lista de activos")
    void getAllActiveServices_ExistenActivos_RetornaListaActivos() {
        // Arrange
        when(serviceOfferingRepository.findByActivoTrue()).thenReturn(List.of(serviceEntity));
        when(serviceOfferingMapper.toDto(serviceEntity)).thenReturn(serviceResponse);

        // Act
        List<ServiceOfferingResponseDTO> resultado = serviceOfferingService.getAllActiveServices();

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getActivo()).isTrue();
    }

    @Test
    @DisplayName("HU12-CP-002: Listar servicios activos sin registros → retorna lista vacía")
    void getAllActiveServices_SinActivos_RetornaListaVacia() {
        // Arrange
        when(serviceOfferingRepository.findByActivoTrue()).thenReturn(List.of());

        // Act
        List<ServiceOfferingResponseDTO> resultado = serviceOfferingService.getAllActiveServices();

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("HU12-CP-003: Listar servicios activos por proveedor público → retorna solo activos de ese proveedor")
    void getActiveServicesByProvider_ProveedorConServicios_RetornaActivos() {
        // Arrange
        when(serviceOfferingRepository.findByIdProveedorAndActivoTrue(proveedorId))
                .thenReturn(List.of(serviceEntity));
        when(serviceOfferingMapper.toDto(serviceEntity)).thenReturn(serviceResponse);

        // Act
        List<ServiceOfferingResponseDTO> resultado = serviceOfferingService.getActiveServicesByProvider(proveedorId);

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getActivo()).isTrue();
    }

    @Test
    @DisplayName("HU12-CP-004: Obtener servicio por ID existente → retorna DTO")
    void getServiceById_IdExistente_RetornaDTO() {
        // Arrange
        when(serviceOfferingRepository.findById(serviceId)).thenReturn(Optional.of(serviceEntity));
        when(serviceOfferingMapper.toDto(serviceEntity)).thenReturn(serviceResponse);

        // Act
        ServiceOfferingResponseDTO resultado = serviceOfferingService.getServiceById(serviceId);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdServicio()).isEqualTo(serviceId);
    }

    @Test
    @DisplayName("HU12-CP-005: Obtener servicio por ID inexistente → lanza ServiceNotFoundException")
    void getServiceById_IdInexistente_LanzaServiceNotFoundException() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();
        when(serviceOfferingRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> serviceOfferingService.getServiceById(idInexistente))
                .isInstanceOf(ServiceNotFoundException.class)
                .hasMessageContaining("Servicio no encontrado");
    }
}
