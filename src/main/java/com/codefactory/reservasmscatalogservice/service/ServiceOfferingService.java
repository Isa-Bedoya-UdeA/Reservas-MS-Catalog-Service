package com.codefactory.reservasmscatalogservice.service;

import com.codefactory.reservasmscatalogservice.dto.request.CreateServiceOfferingRequestDTO;
import com.codefactory.reservasmscatalogservice.dto.request.UpdateServiceOfferingRequestDTO;
import com.codefactory.reservasmscatalogservice.dto.response.ServiceOfferingResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ServiceOfferingService {
    ServiceOfferingResponseDTO createServiceOffering(CreateServiceOfferingRequestDTO request, UUID idProveedorFromJWT);
    ServiceOfferingResponseDTO updateServiceOffering(UUID id, UpdateServiceOfferingRequestDTO request, UUID idProveedorFromJWT);
    void deleteServiceOffering(UUID id, UUID idProveedorFromJWT);
    void permanentlyDeleteServiceOffering(UUID id);
    List<ServiceOfferingResponseDTO> getServicesByProvider(UUID idProveedor, UUID idProveedorFromJWT);
    List<ServiceOfferingResponseDTO> getActiveServicesByCategory(UUID idCategoria);
    List<ServiceOfferingResponseDTO> getAllActiveServices();
}
