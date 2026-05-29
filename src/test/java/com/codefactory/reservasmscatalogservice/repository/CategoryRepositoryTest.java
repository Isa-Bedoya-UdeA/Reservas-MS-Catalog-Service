package com.codefactory.reservasmscatalogservice.repository;

import com.codefactory.reservasmscatalogservice.entity.ServiceCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de integración para CategoryRepository.
 * Utiliza @DataJpaTest con H2 in-memory database.
 */
@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TestEntityManager entityManager;

    private static final String TEST_CATEGORY_NAME = "Belleza y Spa";

    @Nested
    @DisplayName("save")
    class SaveTests {

        @Test
        @DisplayName("Debe guardar categoría con todos los campos")
        void save_NewCategory_SavesAllFields() {
            // Given
            ServiceCategory category = ServiceCategory.builder()
                    .nombreCategoria("Nueva Categoría")
                    .descripcion("Descripción de prueba")
                    .activa(true)
                    .build();

            // When
            ServiceCategory saved = categoryRepository.save(category);
            entityManager.flush();

            // Then
            assertThat(saved.getIdCategoria()).isNotNull();
            assertThat(saved.getNombreCategoria()).isEqualTo("Nueva Categoría");
            assertThat(saved.getDescripcion()).isEqualTo("Descripción de prueba");
            assertThat(saved.getActiva()).isTrue();
            assertThat(saved.getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Debe guardar categoría inactiva por defecto")
        void save_CategoryInactive_DefaultsToInactive() {
            // Given
            ServiceCategory category = ServiceCategory.builder()
                    .nombreCategoria("Categoría Inactiva")
                    .descripcion("Para probar default activa=false")
                    .activa(false)
                    .build();

            // When
            ServiceCategory saved = categoryRepository.save(category);
            entityManager.flush();

            // Then
            assertThat(saved.getActiva()).isFalse();
        }

        @Test
        @DisplayName("Debe establecer fecha de creación automáticamente")
        void save_SetsCreatedAtAutomatically() {
            // Given
            ServiceCategory category = ServiceCategory.builder()
                    .nombreCategoria("AutoDate Category")
                    .build();

            // When
            ServiceCategory saved = categoryRepository.save(category);
            entityManager.flush();

            // Then
            assertThat(saved.getCreatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("findById")
    class FindByIdTests {

        @Test
        @DisplayName("Debe encontrar categoría por ID")
        void findById_ExistingCategory_ReturnsCategory() {
            // Given
            ServiceCategory category = createAndPersistCategory(TEST_CATEGORY_NAME, true);
            UUID categoryId = category.getIdCategoria();

            // When
            Optional<ServiceCategory> found = categoryRepository.findById(categoryId);

            // Then
            assertThat(found).isPresent();
            assertThat(found.get().getNombreCategoria()).isEqualTo(TEST_CATEGORY_NAME);
        }

        @Test
        @DisplayName("Debe retornar empty para ID no existente")
        void findById_NonExistingId_ReturnsEmpty() {
            // When
            Optional<ServiceCategory> found = categoryRepository.findById(UUID.randomUUID());

            // Then
            assertThat(found).isEmpty();
        }
    }

    

    @Nested
    @DisplayName("existsByNombreCategoria")
    class ExistsByNombreCategoriaTests {

        @Test
        @DisplayName("Debe retornar true para nombre existente")
        void existsByNombreCategoria_ExistingName_ReturnsTrue() {
            // Given
            createAndPersistCategory(TEST_CATEGORY_NAME, true);

            // When
            boolean exists = categoryRepository.existsByNombreCategoria(TEST_CATEGORY_NAME);

            // Then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("Debe retornar false para nombre no existente")
        void existsByNombreCategoria_NonExistingName_ReturnsFalse() {
            // When
            boolean exists = categoryRepository.existsByNombreCategoria("NonExistent");

            // Then
            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("findByActivaTrue")
    class FindByActivaTrueTests {

        @Test
        @DisplayName("Debe retornar solo categorías activas")
        void findByActivaTrue_ReturnsOnlyActiveCategories() {
            // Given
            createAndPersistCategory("Categoría Activa 1", true);
            createAndPersistCategory("Categoría Activa 2", true);
            createAndPersistCategory("Categoría Inactiva", false);

            // When
            List<ServiceCategory> activeCategories = categoryRepository.findByActivaTrue();

            // Then
            assertThat(activeCategories).hasSize(2);
            assertThat(activeCategories).allMatch(ServiceCategory::getActiva);
        }

        @Test
        @DisplayName("Debe retornar lista vacía cuando todas están inactivas")
        void findByActivaTrue_AllInactive_ReturnsEmptyList() {
            // Given
            createAndPersistCategory("Categoría Inactiva 1", false);
            createAndPersistCategory("Categoría Inactiva 2", false);

            // When
            List<ServiceCategory> activeCategories = categoryRepository.findByActivaTrue();

            // Then
            assertThat(activeCategories).isEmpty();
        }
    }

    @Nested
    @DisplayName("deleteById")
    class DeleteByIdTests {

        @Test
        @DisplayName("Debe eliminar categoría por ID")
        void deleteById_ExistingCategory_DeletesCategory() {
            // Given
            ServiceCategory category = createAndPersistCategory(TEST_CATEGORY_NAME, true);
            UUID categoryId = category.getIdCategoria();

            // When
            categoryRepository.deleteById(categoryId);
            entityManager.flush();

            // Then
            Optional<ServiceCategory> found = categoryRepository.findById(categoryId);
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAll")
    class FindAllTests {

        @Test
        @DisplayName("Debe retornar todas las categorías")
        void findAll_ReturnsAllCategories() {
            // Given
            createAndPersistCategory("Categoría 1", true);
            createAndPersistCategory("Categoría 2", false);
            createAndPersistCategory("Categoría 3", true);

            // When
            List<ServiceCategory> allCategories = categoryRepository.findAll();

            // Then
            assertThat(allCategories).hasSize(3);
        }

        @Test
        @DisplayName("Debe retornar lista vacía cuando no hay categorías")
        void findAll_Empty_ReturnsEmptyList() {
            // When
            List<ServiceCategory> allCategories = categoryRepository.findAll();

            // Then
            assertThat(allCategories).isEmpty();
        }
    }

    @Nested
    @DisplayName("count")
    class CountTests {

        @Test
        @DisplayName("Debe retornar count correcto")
        void count_ReturnsCorrectCount() {
            // Given
            long initialCount = categoryRepository.count();
            createAndPersistCategory("Count 1", true);
            createAndPersistCategory("Count 2", false);
            entityManager.flush();

            // When
            long newCount = categoryRepository.count();

            // Then
            assertThat(newCount).isEqualTo(initialCount + 2);
        }
    }

    // Helper methods
    private ServiceCategory createAndPersistCategory(String name, boolean active) {
        ServiceCategory category = ServiceCategory.builder()
                .nombreCategoria(name)
                .descripcion("Descripción de " + name)
                .activa(active)
                .build();
        return entityManager.persist(category);
    }
}