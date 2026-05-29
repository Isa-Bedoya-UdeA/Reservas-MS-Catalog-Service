package com.codefactory.reservasmscatalogservice.controller;

import com.codefactory.reservasmscatalogservice.dto.request.CreateCategoryRequestDTO;
import com.codefactory.reservasmscatalogservice.dto.request.UpdateCategoryRequestDTO;
import com.codefactory.reservasmscatalogservice.dto.response.CategoryResponseDTO;
import com.codefactory.reservasmscatalogservice.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CategoryController using @ExtendWith(MockitoExtension).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MS-Catalog - CategoryController (Unit)")
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private UUID categoryId;
    private CategoryResponseDTO categoryResponse;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();
        categoryResponse = CategoryResponseDTO.builder()
                .idCategoria(categoryId)
                .nombreCategoria("Belleza y Spa")
                .descripcion("Servicios de belleza")
                .activa(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("getAllCategories returns list of categories")
    void getAllCategories_ReturnsList() {
        when(categoryService.getAllCategories()).thenReturn(List.of(categoryResponse));
        
        ResponseEntity<List<CategoryResponseDTO>> response = categoryController.getAllCategories();
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getNombreCategoria()).isEqualTo("Belleza y Spa");
    }

    @Test
    @DisplayName("getActiveCategories returns only active categories")
    void getActiveCategories_ReturnsOnlyActive() {
        when(categoryService.getActiveCategories()).thenReturn(List.of(categoryResponse));
        
        ResponseEntity<List<CategoryResponseDTO>> response = categoryController.getActiveCategories();
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).allMatch(CategoryResponseDTO::getActiva);
    }

    @Test
    @DisplayName("getCategoryById returns category when found")
    void getCategoryById_ReturnsCategory() {
        when(categoryService.getCategoryById(categoryId)).thenReturn(categoryResponse);
        
        ResponseEntity<CategoryResponseDTO> response = categoryController.getCategoryById(categoryId);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getIdCategoria()).isEqualTo(categoryId);
    }

    @Test
    @DisplayName("createCategory returns CREATED status")
    void createCategory_ReturnsCreated() {
        CreateCategoryRequestDTO request = CreateCategoryRequestDTO.builder()
                .nombreCategoria("Nueva Categoría")
                .descripcion("Descripción")
                .build();
        
        when(categoryService.createCategory(any())).thenReturn(categoryResponse);
        
        ResponseEntity<CategoryResponseDTO> response = categoryController.createCategory(request);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("updateCategory returns updated category")
    void updateCategory_ReturnsUpdated() {
        UpdateCategoryRequestDTO request = UpdateCategoryRequestDTO.builder()
                .nombreCategoria("Actualizado")
                .build();
        
        CategoryResponseDTO updated = CategoryResponseDTO.builder()
                .idCategoria(categoryId)
                .nombreCategoria("Actualizado")
                .activa(true)
                .build();
        
        when(categoryService.updateCategory(eq(categoryId), any())).thenReturn(updated);
        
        ResponseEntity<CategoryResponseDTO> response = categoryController.updateCategory(categoryId, request);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getNombreCategoria()).isEqualTo("Actualizado");
    }

    @Test
    @DisplayName("deactivateCategory returns NO_CONTENT")
    void deactivateCategory_ReturnsNoContent() {
        doNothing().when(categoryService).deactivateCategory(categoryId);
        
        ResponseEntity<Void> response = categoryController.deactivateCategory(categoryId);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(categoryService).deactivateCategory(categoryId);
    }

    @Test
    @DisplayName("activateCategory returns NO_CONTENT")
    void activateCategory_ReturnsNoContent() {
        doNothing().when(categoryService).activateCategory(categoryId);
        
        ResponseEntity<Void> response = categoryController.activateCategory(categoryId);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(categoryService).activateCategory(categoryId);
    }
}