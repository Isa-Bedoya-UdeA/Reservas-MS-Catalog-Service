package com.codefactory.reservasmscatalogservice.service;

import com.codefactory.reservasmscatalogservice.client.AuthClient;
import com.codefactory.reservasmscatalogservice.dto.request.CreateCategoryRequestDTO;
import com.codefactory.reservasmscatalogservice.dto.request.UpdateCategoryRequestDTO;
import com.codefactory.reservasmscatalogservice.dto.response.CategoryResponseDTO;
import com.codefactory.reservasmscatalogservice.entity.ServiceCategory;
import com.codefactory.reservasmscatalogservice.exception.BusinessException;
import com.codefactory.reservasmscatalogservice.exception.ResourceNotFoundException;
import com.codefactory.reservasmscatalogservice.mapper.CategoryMapper;
import com.codefactory.reservasmscatalogservice.repository.CategoryRepository;
import com.codefactory.reservasmscatalogservice.repository.ServiceOfferingRepository;
import com.codefactory.reservasmscatalogservice.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias - MS-CATALOG-SERVICE
 * Sprint 2 - CategoryServiceImpl
 *
 * HU04: Creación de categorías de servicios
 * HU05: Edición de categorías de servicios
 * HU06: Eliminar / desactivar categoría (Soft Delete)
 * HU07: Listar categorías (con filtros activo/inactivo)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MS-Catalog - CategoryServiceImpl")
class CategoryServiceImplTest {

    @Mock private CategoryRepository categoryRepository;
    @Mock private CategoryMapper categoryMapper;
    @Mock private AuthClient authClient;
    @Mock private ServiceOfferingRepository serviceOfferingRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private UUID categoryId;
    private ServiceCategory categoryEntity;
    private CategoryResponseDTO categoryResponse;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();

        categoryEntity = ServiceCategory.builder()
                .idCategoria(categoryId)
                .nombreCategoria("Belleza y Spa")
                .descripcion("Servicios de belleza y bienestar")
                .activa(true)
                .createdAt(LocalDateTime.now())
                .build();

        categoryResponse = CategoryResponseDTO.builder()
                .idCategoria(categoryId)
                .nombreCategoria("Belleza y Spa")
                .descripcion("Servicios de belleza y bienestar")
                .activa(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // =========================================================================
    // HU04 - Creación de categorías
    // =========================================================================

    @Test
    @DisplayName("HU04-CP-001: Crear categoría con nombre único → retorna DTO con datos correctos")
    void createCategory_NombreUnico_RetornaCategoryDTO() {
        // Arrange
        CreateCategoryRequestDTO request = CreateCategoryRequestDTO.builder()
                .nombreCategoria("Belleza y Spa")
                .descripcion("Servicios de belleza y bienestar")
                .build();

        when(categoryRepository.existsByNombreCategoria("Belleza y Spa")).thenReturn(false);
        when(categoryMapper.toEntity(request)).thenReturn(categoryEntity);
        when(categoryRepository.save(categoryEntity)).thenReturn(categoryEntity);
        when(categoryMapper.toDto(categoryEntity)).thenReturn(categoryResponse);

        // Act
        CategoryResponseDTO resultado = categoryService.createCategory(request);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombreCategoria()).isEqualTo("Belleza y Spa");
        assertThat(resultado.getActiva()).isTrue();
        verify(categoryRepository, times(1)).save(any(ServiceCategory.class));
    }

    @Test
    @DisplayName("HU04-CP-002: Crear categoría con nombre duplicado → lanza BusinessException")
    void createCategory_NombreDuplicado_LanzaBusinessException() {
        // Arrange
        CreateCategoryRequestDTO request = CreateCategoryRequestDTO.builder()
                .nombreCategoria("Belleza y Spa")
                .build();

        when(categoryRepository.existsByNombreCategoria("Belleza y Spa")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> categoryService.createCategory(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Ya existe una categoría con el nombre");

        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("HU04-CP-003 (Caja blanca - rama): nombre duplicado nunca llega a save()")
    void createCategory_RamaDuplicado_NuncaLlamaASave() {
        // Arrange
        CreateCategoryRequestDTO request = CreateCategoryRequestDTO.builder()
                .nombreCategoria("Duplicada")
                .build();
        when(categoryRepository.existsByNombreCategoria("Duplicada")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> categoryService.createCategory(request))
                .isInstanceOf(BusinessException.class);
        verify(categoryRepository, never()).save(any());
        verify(categoryMapper, never()).toEntity(any());
    }

    @Test
    @DisplayName("HU04-CP-004 (Caja blanca - rama): nombre único → llama a save exactamente una vez")
    void createCategory_RamaNuevo_LlamaASaveUnaVez() {
        // Arrange
        CreateCategoryRequestDTO request = CreateCategoryRequestDTO.builder()
                .nombreCategoria("Nueva Categoría")
                .build();
        ServiceCategory nuevaEntidad = ServiceCategory.builder()
                .nombreCategoria("Nueva Categoría").activa(true).build();

        when(categoryRepository.existsByNombreCategoria("Nueva Categoría")).thenReturn(false);
        when(categoryMapper.toEntity(request)).thenReturn(nuevaEntidad);
        when(categoryRepository.save(nuevaEntidad)).thenReturn(nuevaEntidad);
        when(categoryMapper.toDto(nuevaEntidad)).thenReturn(categoryResponse);

        // Act
        categoryService.createCategory(request);

        // Assert
        verify(categoryRepository, times(1)).save(any(ServiceCategory.class));
    }

    // =========================================================================
    // HU05 - Edición de categorías
    // =========================================================================

    @Test
    @DisplayName("HU05-CP-001: Actualizar nombre y descripción con datos válidos → retorna DTO actualizado")
    void updateCategory_DatosValidos_RetornaDTOActualizado() {
        // Arrange
        UpdateCategoryRequestDTO request = UpdateCategoryRequestDTO.builder()
                .nombreCategoria("Spa Premium")
                .descripcion("Descripción actualizada")
                .build();

        ServiceCategory actualizada = ServiceCategory.builder()
                .idCategoria(categoryId)
                .nombreCategoria("Spa Premium")
                .descripcion("Descripción actualizada")
                .activa(true)
                .build();

        CategoryResponseDTO respuestaActualizada = CategoryResponseDTO.builder()
                .idCategoria(categoryId)
                .nombreCategoria("Spa Premium")
                .activa(true)
                .build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(categoryEntity));
        when(categoryRepository.existsByNombreCategoria("Spa Premium")).thenReturn(false);
        when(categoryRepository.save(any())).thenReturn(actualizada);
        when(categoryMapper.toDto(any())).thenReturn(respuestaActualizada);

        // Act
        CategoryResponseDTO resultado = categoryService.updateCategory(categoryId, request);

        // Assert
        assertThat(resultado.getNombreCategoria()).isEqualTo("Spa Premium");
        verify(categoryRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("HU05-CP-002: Actualizar con nombre ya usado por otra categoría → lanza BusinessException")
    void updateCategory_NombreConflicto_LanzaBusinessException() {
        // Arrange
        UpdateCategoryRequestDTO request = UpdateCategoryRequestDTO.builder()
                .nombreCategoria("Salud y Bienestar")
                .build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(categoryEntity));
        when(categoryRepository.existsByNombreCategoria("Salud y Bienestar")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> categoryService.updateCategory(categoryId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Ya existe una categoría con el nombre");

        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("HU05-CP-003: Actualizar con el mismo nombre actual (sin cambio) → no lanza excepción")
    void updateCategory_MismoNombre_NoLanzaExcepcion() {
        // Arrange - mismo nombre que ya tiene la categoría
        UpdateCategoryRequestDTO request = UpdateCategoryRequestDTO.builder()
                .nombreCategoria("Belleza y Spa") // mismo nombre
                .descripcion("Nueva descripción")
                .build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(categoryEntity));
        when(categoryRepository.save(any())).thenReturn(categoryEntity);
        when(categoryMapper.toDto(any())).thenReturn(categoryResponse);

        // Act & Assert - no debe lanzar excepción aunque existsByNombreCategoria no se llame
        assertThatCode(() -> categoryService.updateCategory(categoryId, request))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("HU05-CP-004: Actualizar categoría inexistente → lanza ResourceNotFoundException")
    void updateCategory_IdInexistente_LanzaResourceNotFoundException() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();
        when(categoryRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> categoryService.updateCategory(idInexistente, new UpdateCategoryRequestDTO()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Categoría no encontrado");
    }

    @Test
    @DisplayName("HU05-CP-005: Actualizar solo descripción (nombre null) → no valida nombre duplicado")
    void updateCategory_SoloDescripcion_NoValidaNombre() {
        // Arrange
        UpdateCategoryRequestDTO request = UpdateCategoryRequestDTO.builder()
                .descripcion("Solo actualizo descripción")
                .build(); // nombreCategoria = null

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(categoryEntity));
        when(categoryRepository.save(any())).thenReturn(categoryEntity);
        when(categoryMapper.toDto(any())).thenReturn(categoryResponse);

        // Act
        categoryService.updateCategory(categoryId, request);

        // Assert - nunca debe validar nombre si no viene en el request
        verify(categoryRepository, never()).existsByNombreCategoria(anyString());
    }

    // =========================================================================
    // HU06 - Soft Delete (desactivar categoría)
    // =========================================================================

    @Test
    @DisplayName("HU06-CP-001: Desactivar categoría activa → queda inactiva, llama a save")
    void deactivateCategory_CategoriaActiva_DesactivaYGuarda() {
        // Arrange
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(categoryEntity));
        when(categoryRepository.save(any())).thenReturn(categoryEntity);

        // Act
        categoryService.deactivateCategory(categoryId);

        // Assert
        assertThat(categoryEntity.getActiva()).isFalse();
        verify(categoryRepository, times(1)).save(categoryEntity);
    }

    @Test
    @DisplayName("HU06-CP-002: Desactivar categoría ya inactiva → lanza BusinessException")
    void deactivateCategory_CategoriaYaInactiva_LanzaBusinessException() {
        // Arrange
        categoryEntity.setActiva(false);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(categoryEntity));

        // Act & Assert
        assertThatThrownBy(() -> categoryService.deactivateCategory(categoryId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ya está inactiva");

        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("HU06-CP-003: Desactivar categoría inexistente → lanza ResourceNotFoundException")
    void deactivateCategory_IdInexistente_LanzaResourceNotFoundException() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();
        when(categoryRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> categoryService.deactivateCategory(idInexistente))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("HU06-CP-004: Activar categoría inactiva → queda activa, llama a save")
    void activateCategory_CategoriaInactiva_ActivaYGuarda() {
        // Arrange
        categoryEntity.setActiva(false);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(categoryEntity));
        when(categoryRepository.save(any())).thenReturn(categoryEntity);

        // Act
        categoryService.activateCategory(categoryId);

        // Assert
        assertThat(categoryEntity.getActiva()).isTrue();
        verify(categoryRepository, times(1)).save(categoryEntity);
    }

    @Test
    @DisplayName("HU06-CP-005: Activar categoría ya activa → lanza BusinessException")
    void activateCategory_CategoriaYaActiva_LanzaBusinessException() {
        // Arrange - categoría activa por defecto en setUp
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(categoryEntity));

        // Act & Assert
        assertThatThrownBy(() -> categoryService.activateCategory(categoryId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ya está activa");
    }

    // =========================================================================
    // HU07 - Listar categorías con filtros
    // =========================================================================

    @Test
    @DisplayName("HU07-CP-001: Listar todas las categorías → retorna lista completa")
    void getAllCategories_ExistenCategorias_RetornaListaCompleta() {
        // Arrange
        ServiceCategory cat2 = ServiceCategory.builder()
                .idCategoria(UUID.randomUUID())
                .nombreCategoria("Salud")
                .activa(false)
                .build();

        when(categoryRepository.findAll()).thenReturn(List.of(categoryEntity, cat2));
        when(categoryMapper.toDto(categoryEntity)).thenReturn(categoryResponse);
        when(categoryMapper.toDto(cat2)).thenReturn(CategoryResponseDTO.builder()
                .nombreCategoria("Salud").activa(false).build());

        // Act
        List<CategoryResponseDTO> resultado = categoryService.getAllCategories();

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting("nombreCategoria")
                .containsExactlyInAnyOrder("Belleza y Spa", "Salud");
    }

    @Test
    @DisplayName("HU07-CP-002: Listar solo categorías activas → retorna solo activas")
    void getActiveCategories_ExistenActivas_RetornaSoloActivas() {
        // Arrange
        when(categoryRepository.findByActivaTrue()).thenReturn(List.of(categoryEntity));
        when(categoryMapper.toDto(categoryEntity)).thenReturn(categoryResponse);

        // Act
        List<CategoryResponseDTO> resultado = categoryService.getActiveCategories();

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getActiva()).isTrue();
        verify(categoryRepository, times(1)).findByActivaTrue();
        verify(categoryRepository, never()).findAll();
    }

    @Test
    @DisplayName("HU07-CP-003: Listar categorías con repositorio vacío → retorna lista vacía")
    void getAllCategories_SinCategorias_RetornaListaVacia() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(List.of());

        // Act
        List<CategoryResponseDTO> resultado = categoryService.getAllCategories();

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("HU07-CP-004: Buscar categoría por ID existente → retorna DTO")
    void getCategoryById_IdExistente_RetornaDTO() {
        // Arrange
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(categoryEntity));
        when(categoryMapper.toDto(categoryEntity)).thenReturn(categoryResponse);

        // Act
        CategoryResponseDTO resultado = categoryService.getCategoryById(categoryId);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdCategoria()).isEqualTo(categoryId);
    }

    @Test
    @DisplayName("HU07-CP-005: Buscar categoría por ID inexistente → lanza ResourceNotFoundException")
    void getCategoryById_IdInexistente_LanzaResourceNotFoundException() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();
        when(categoryRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> categoryService.getCategoryById(idInexistente))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Categoría no encontrado");
    }
}
