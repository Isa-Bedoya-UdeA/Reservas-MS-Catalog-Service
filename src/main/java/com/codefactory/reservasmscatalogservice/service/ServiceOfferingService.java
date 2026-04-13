package com.codefactory.reservasmscatalogservice.service;

import com.codefactory.reservasmscatalogservice.dto.request.CreateServiceOfferingRequestDTO;
import com.codefactory.reservasmscatalogservice.dto.response.ServiceOfferingResponseDTO;

import java.util.UUID;

public interface ServiceOfferingService {
    ServiceOfferingResponseDTO createServiceOffering(CreateServiceOfferingRequestDTO request, UUID idProveedor);
}
