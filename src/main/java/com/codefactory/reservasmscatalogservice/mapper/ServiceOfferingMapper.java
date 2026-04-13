package com.codefactory.reservasmscatalogservice.mapper;

import com.codefactory.reservasmscatalogservice.dto.request.CreateServiceOfferingRequestDTO;
import com.codefactory.reservasmscatalogservice.dto.response.ServiceOfferingResponseDTO;
import com.codefactory.reservasmscatalogservice.entity.ServiceOffering;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServiceOfferingMapper {
    ServiceOffering toEntity(CreateServiceOfferingRequestDTO requestDTO);

    @Mapping(target = "proveedorInfo", ignore = true)
    ServiceOfferingResponseDTO toDto(ServiceOffering entity);
}
