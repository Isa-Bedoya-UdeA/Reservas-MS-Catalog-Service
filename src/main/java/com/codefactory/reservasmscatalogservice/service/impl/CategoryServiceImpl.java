package com.codefactory.reservasmscatalogservice.service.impl;

import com.codefactory.reservasmscatalogservice.client.AuthClient;
import com.codefactory.reservasmscatalogservice.dto.external.ExternalProviderDTO;
import com.codefactory.reservasmscatalogservice.dto.request.CreateCategoryRequestDTO;
import com.codefactory.reservasmscatalogservice.dto.request.UpdateCategoryRequestDTO;
import com.codefactory.reservasmscatalogservice.dto.response.CategoryResponseDTO;
import com.codefactory.reservasmscatalogservice.entity.ServiceCategory;
import com.codefactory.reservasmscatalogservice.entity.ServiceOffering;
import com.codefactory.reservasmscatalogservice.exception.BusinessException;
import com.codefactory.reservasmscatalogservice.exception.ResourceNotFoundException;
import com.codefactory.reservasmscatalogservice.mapper.CategoryMapper;
import com.codefactory.reservasmscatalogservice.repository.CategoryRepository;
import com.codefactory.reservasmscatalogservice.repository.ServiceOfferingRepository;
import com.codefactory.reservasmscatalogservice.service.CategoryService;
import com.codefactory.reservasmscatalogservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final EmailService emailService;
    private final AuthClient authClient;
    private final ServiceOfferingRepository serviceOfferingRepository;

    @Override
    @Transactional
    public CategoryResponseDTO createCategory(CreateCategoryRequestDTO request) {
        // Check if category name already exists
        if (categoryRepository.existsByNombreCategoria(request.getNombreCategoria())) {
            throw new BusinessException("Ya existe una categoría con el nombre: " + request.getNombreCategoria());
        }
        ServiceCategory category = categoryMapper.toEntity(request);
        ServiceCategory savedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(savedCategory);
    }

    @Override
    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public List<CategoryResponseDTO> getActiveCategories() {
        return categoryRepository.findByActivaTrue().stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public CategoryResponseDTO getCategoryById(UUID id) {
        ServiceCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", id));
        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional
    public CategoryResponseDTO updateCategory(UUID id, UpdateCategoryRequestDTO request) {
        ServiceCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", id));

        if (request.getNombreCategoria() != null) {
            // Check if new name conflicts with existing category
            if (!category.getNombreCategoria().equals(request.getNombreCategoria()) &&
                    categoryRepository.existsByNombreCategoria(request.getNombreCategoria())) {
                throw new BusinessException("Ya existe una categoría con el nombre: " + request.getNombreCategoria());
            }
            category.setNombreCategoria(request.getNombreCategoria());
        }

        if (request.getDescripcion() != null) {
            category.setDescripcion(request.getDescripcion());
        }

        if (request.getActiva() != null) {
            category.setActiva(request.getActiva());
        }

        ServiceCategory updatedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(updatedCategory);
    }

    @Override
    @Transactional
    public void deactivateCategory(UUID id) {
        ServiceCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", id));
        if (!category.getActiva()) {
            throw new BusinessException("La categoría ya está inactiva");
        }
        category.setActiva(false);
        categoryRepository.save(category);

        // Notify all providers who belong to this category
        notifyProvidersAboutCategoryDeactivation(id, category.getNombreCategoria());
    }

    private void notifyProvidersAboutCategoryDeactivation(UUID categoryId, String categoryName) {
        // Get all services to find providers in this category
        List<ServiceOffering> services = serviceOfferingRepository.findByActivoTrue();
        Set<UUID> notifiedProviders = new HashSet<>();

        for (ServiceOffering service : services) {
            UUID providerId = service.getIdProveedor();
            if (notifiedProviders.contains(providerId)) {
                continue; // Already notified this provider
            }

            try {
                var providerResponse = authClient.getProviderById(providerId);
                if (providerResponse != null && providerResponse.getBody() != null) {
                    ExternalProviderDTO provider = providerResponse.getBody();
                    if (categoryId.equals(provider.getIdCategoria())) {
                        // Send email notification
                        emailService.sendCategoryDeactivationEmail(
                                provider.getEmail(),
                                provider.getNombreComercial(),
                                categoryName
                        );
                        notifiedProviders.add(providerId);
                        log.info("Sent category deactivation email to provider: {}", provider.getNombreComercial());
                    }
                }
            } catch (Exception e) {
                log.error("Failed to notify provider {} about category deactivation", providerId, e);
            }
        }
    }

    @Override
    @Transactional
    public void activateCategory(UUID id) {
        ServiceCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", id));
        if (category.getActiva()) {
            throw new BusinessException("La categoría ya está activa");
        }
        category.setActiva(true);
        categoryRepository.save(category);
    }
}
