package com.codefactory.reservasmscatalogservice.mapper;

import com.codefactory.reservasmscatalogservice.dto.request.CreateCategoryRequestDTO;
import com.codefactory.reservasmscatalogservice.dto.response.CategoryResponseDTO;
import com.codefactory.reservasmscatalogservice.entity.ServiceCategory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    ServiceCategory toEntity(CreateCategoryRequestDTO requestDTO);

    CategoryResponseDTO toDto(ServiceCategory entity);
}
