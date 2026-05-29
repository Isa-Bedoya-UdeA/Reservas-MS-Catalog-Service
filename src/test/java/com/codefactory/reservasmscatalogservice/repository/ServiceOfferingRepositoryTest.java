package com.codefactory.reservasmscatalogservice.repository;

import com.codefactory.reservasmscatalogservice.entity.ServiceOffering;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de integración para ServiceOfferingRepository.
 * Utiliza @DataJpaTest con H2 in-memory database.
 */
@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
class ServiceOfferingRepositoryTest {

    @Autowired
    private ServiceOfferingRepository serviceOfferingRepository;

    @Autowired
    private TestEntityManager entityManager;

    private UUID providerId;

    @BeforeEach
    void setUp() {
        providerId = UUID.randomUUID();
    }

    @Nested
    @DisplayName("save")
    class SaveTests {

        @Test
        @DisplayName("Debe guardar servicio con todos los campos")
        void save_NewService_SavesAllFields() {
            // Given
            ServiceOffering service = ServiceOffering.builder()
                    .idProveedor(providerId)
                    .nombreServicio("Corte de cabello")
                    .duracionMinutos(30)
                    .precio(new BigDecimal("25.00"))
                    .descripcion("Corte básico para hombre")
                    .activo(true)
                    .capacidadMaxima(5)
                    .build();

            // When
            ServiceOffering saved = serviceOfferingRepository.save(service);
            entityManager.flush();

            // Then
            assertThat(saved.getIdServicio()).isNotNull();
            assertThat(saved.getNombreServicio()).isEqualTo("Corte de cabello");
            assertThat(saved.getDuracionMinutos()).isEqualTo(30);
            assertThat(saved.getPrecio()).isEqualByComparingTo(new BigDecimal("25.00"));
            assertThat(saved.getActivo()).isTrue();
            assertThat(saved.getCapacidadMaxima()).isEqualTo(5);
            assertThat(saved.getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Debe guardar servicio inactivo por defecto")
        void save_ServiceInactive_DefaultsToInactive() {
            // Given
            ServiceOffering service = ServiceOffering.builder()
                    .idProveedor(providerId)
                    .nombreServicio("Servicio Inactivo")
                    .duracionMinutos(60)
                    .precio(new BigDecimal("50.00"))
                    .activo(false)
                    .build();

            // When
            ServiceOffering saved = serviceOfferingRepository.save(service);
            entityManager.flush();

            // Then
            assertThat(saved.getActivo()).isFalse();
        }

        @Test
        @DisplayName("Debe guardar servicio con capacidad máxima default 1")
        void save_ServiceWithoutCapacity_SetsDefault() {
            // Given
            ServiceOffering service = ServiceOffering.builder()
                    .idProveedor(providerId)
                    .nombreServicio("Servicio Default")
                    .duracionMinutos(45)
                    .precio(new BigDecimal("30.00"))
                    .build();

            // When
            ServiceOffering saved = serviceOfferingRepository.save(service);
            entityManager.flush();

            // Then
            assertThat(saved.getCapacidadMaxima()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("findById")
    class FindByIdTests {

        @Test
        @DisplayName("Debe encontrar servicio por ID")
        void findById_ExistingService_ReturnsService() {
            // Given
            ServiceOffering service = createAndPersistService("Servicio 1", true);
            UUID serviceId = service.getIdServicio();

            // When
            Optional<ServiceOffering> found = serviceOfferingRepository.findById(serviceId);

            // Then
            assertThat(found).isPresent();
            assertThat(found.get().getNombreServicio()).isEqualTo("Servicio 1");
        }

        @Test
        @DisplayName("Debe retornar empty para ID no existente")
        void findById_NonExistingId_ReturnsEmpty() {
            // When
            Optional<ServiceOffering> found = serviceOfferingRepository.findById(UUID.randomUUID());

            // Then
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByActivoTrue")
    class FindByActivoTrueTests {

        @Test
        @DisplayName("Debe retornar solo servicios activos")
        void findByActivoTrue_ReturnsOnlyActiveServices() {
            // Given
            createAndPersistService("Servicio Activo 1", true);
            createAndPersistService("Servicio Activo 2", true);
            createAndPersistService("Servicio Inactivo", false);

            // When
            List<ServiceOffering> activeServices = serviceOfferingRepository.findByActivoTrue();

            // Then
            assertThat(activeServices).hasSize(2);
            assertThat(activeServices).allMatch(ServiceOffering::getActivo);
        }

        @Test
        @DisplayName("Debe retornar lista vacía cuando todos están inactivos")
        void findByActivoTrue_AllInactive_ReturnsEmptyList() {
            // Given
            createAndPersistService("Servicio Inactivo 1", false);
            createAndPersistService("Servicio Inactivo 2", false);

            // When
            List<ServiceOffering> activeServices = serviceOfferingRepository.findByActivoTrue();

            // Then
            assertThat(activeServices).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByIdProveedor")
    class FindByIdProveedorTests {

        @Test
        @DisplayName("Debe retornar servicios de un proveedor específico")
        void findByIdProveedor_ExistingProvider_ReturnsServices() {
            // Given
            UUID otherProvider = UUID.randomUUID();
            createAndPersistServiceWithProvider("Servicio Provider 1", providerId, true);
            createAndPersistServiceWithProvider("Servicio Provider 2", providerId, true);
            createAndPersistServiceWithProvider("Otro Proveedor", otherProvider, true);

            // When
            List<ServiceOffering> providerServices = serviceOfferingRepository.findByIdProveedor(providerId);

            // Then
            assertThat(providerServices).hasSize(2);
            assertThat(providerServices).allMatch(s -> s.getIdProveedor().equals(providerId));
        }

        @Test
        @DisplayName("Debe retornar lista vacía para proveedor sin servicios")
        void findByIdProveedor_NoServices_ReturnsEmptyList() {
            // When
            List<ServiceOffering> providerServices = serviceOfferingRepository.findByIdProveedor(UUID.randomUUID());

            // Then
            assertThat(providerServices).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByIdProveedorAndActivoTrue")
    class FindByIdProveedorAndActivoTrueTests {

        @Test
        @DisplayName("Debe retornar solo servicios activos de un proveedor")
        void findByIdProveedorAndActivoTrue_ReturnsOnlyActiveServices() {
            // Given
            createAndPersistServiceWithProvider("Activo Provider", providerId, true);
            createAndPersistServiceWithProvider("Inactivo Provider", providerId, false);

            // When
            List<ServiceOffering> activeServices = serviceOfferingRepository.findByIdProveedorAndActivoTrue(providerId);

            // Then
            assertThat(activeServices).hasSize(1);
            assertThat(activeServices.get(0).getActivo()).isTrue();
        }

        @Test
        @DisplayName("Debe retornar lista vacía si todos los servicios del provider están inactivos")
        void findByIdProveedorAndActivoTrue_AllInactive_ReturnsEmptyList() {
            // Given
            createAndPersistServiceWithProvider("Inactivo 1", providerId, false);
            createAndPersistServiceWithProvider("Inactivo 2", providerId, false);

            // When
            List<ServiceOffering> activeServices = serviceOfferingRepository.findByIdProveedorAndActivoTrue(providerId);

            // Then
            assertThat(activeServices).isEmpty();
        }
    }

    @Nested
    @DisplayName("deleteById")
    class DeleteByIdTests {

        @Test
        @DisplayName("Debe eliminar servicio por ID")
        void deleteById_ExistingService_DeletesService() {
            // Given
            ServiceOffering service = createAndPersistService("Servicio para eliminar", true);
            UUID serviceId = service.getIdServicio();

            // When
            serviceOfferingRepository.deleteById(serviceId);
            entityManager.flush();

            // Then
            Optional<ServiceOffering> found = serviceOfferingRepository.findById(serviceId);
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAll")
    class FindAllTests {

        @Test
        @DisplayName("Debe retornar todos los servicios")
        void findAll_ReturnsAllServices() {
            // Given
            createAndPersistService("Servicio 1", true);
            createAndPersistService("Servicio 2", false);
            createAndPersistService("Servicio 3", true);

            // When
            List<ServiceOffering> allServices = serviceOfferingRepository.findAll();

            // Then
            assertThat(allServices).hasSize(3);
        }

        @Test
        @DisplayName("Debe retornar lista vacía cuando no hay servicios")
        void findAll_Empty_ReturnsEmptyList() {
            // When
            List<ServiceOffering> allServices = serviceOfferingRepository.findAll();

            // Then
            assertThat(allServices).isEmpty();
        }
    }

    @Nested
    @DisplayName("count")
    class CountTests {

        @Test
        @DisplayName("Debe retornar count correcto")
        void count_ReturnsCorrectCount() {
            // Given
            long initialCount = serviceOfferingRepository.count();
            createAndPersistService("Count 1", true);
            createAndPersistService("Count 2", false);
            entityManager.flush();

            // When
            long newCount = serviceOfferingRepository.count();

            // Then
            assertThat(newCount).isEqualTo(initialCount + 2);
        }
    }

    // Helper methods
    private ServiceOffering createAndPersistService(String name, boolean active) {
        return createAndPersistServiceWithProvider(name, providerId, active);
    }

    private ServiceOffering createAndPersistServiceWithProvider(String name, UUID provId, boolean active) {
        ServiceOffering service = ServiceOffering.builder()
                .idProveedor(provId)
                .nombreServicio(name)
                .duracionMinutos(30)
                .precio(new BigDecimal("25.00"))
                .descripcion("Descripción de " + name)
                .activo(active)
                .capacidadMaxima(5)
                .build();
        return entityManager.persist(service);
    }
}