package com.codefactory.reservasmscatalogservice.service;

import com.codefactory.reservasmscatalogservice.dto.request.CreateCategoryRequestDTO;
import com.codefactory.reservasmscatalogservice.dto.request.UpdateCategoryRequestDTO;
import com.codefactory.reservasmscatalogservice.dto.response.CategoryResponseDTO;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    CategoryResponseDTO createCategory(CreateCategoryRequestDTO request);
    List<CategoryResponseDTO> getAllCategories();
    List<CategoryResponseDTO> getActiveCategories();
    CategoryResponseDTO getCategoryById(UUID id);
    CategoryResponseDTO updateCategory(UUID id, UpdateCategoryRequestDTO request);
    void deactivateCategory(UUID id);
    void activateCategory(UUID id);
}
