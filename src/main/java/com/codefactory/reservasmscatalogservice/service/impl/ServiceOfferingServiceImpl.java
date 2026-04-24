package com.codefactory.reservasmscatalogservice.service.impl;

import com.codefactory.reservasmscatalogservice.client.AuthClient;
import com.codefactory.reservasmscatalogservice.dto.request.CreateServiceOfferingRequestDTO;
import com.codefactory.reservasmscatalogservice.dto.request.UpdateServiceOfferingRequestDTO;
import com.codefactory.reservasmscatalogservice.dto.response.CategoryResponseDTO;
import com.codefactory.reservasmscatalogservice.dto.response.ServiceOfferingResponseDTO;
import com.codefactory.reservasmscatalogservice.entity.ServiceOffering;
import com.codefactory.reservasmscatalogservice.exception.CategoryInactiveException;
import com.codefactory.reservasmscatalogservice.exception.ProviderMismatchException;
import com.codefactory.reservasmscatalogservice.exception.ServiceAlreadyInactiveException;
import com.codefactory.reservasmscatalogservice.exception.ServiceNotFoundException;
import com.codefactory.reservasmscatalogservice.mapper.ServiceOfferingMapper;
import com.codefactory.reservasmscatalogservice.repository.ServiceOfferingRepository;
import com.codefactory.reservasmscatalogservice.service.CategoryService;
import com.codefactory.reservasmscatalogservice.service.ServiceOfferingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServiceOfferingServiceImpl implements ServiceOfferingService {

    private final ServiceOfferingRepository serviceOfferingRepository;
    private final ServiceOfferingMapper serviceOfferingMapper;
    private final CategoryService categoryService;
    private final AuthClient authClient;

    @Override
    @Transactional
    public ServiceOfferingResponseDTO createServiceOffering(CreateServiceOfferingRequestDTO request, UUID idProveedorFromJWT) {
        // Validate provider exists
        try {
            authClient.getProviderById(idProveedorFromJWT);
        } catch (Exception e) {
            throw new ServiceNotFoundException("Proveedor no encontrado con id: " + idProveedorFromJWT);
        }

        ServiceOffering serviceOffering = serviceOfferingMapper.toEntity(request);
        serviceOffering.setIdProveedor(idProveedorFromJWT);
        serviceOffering.setActivo(true);
        ServiceOffering savedService = serviceOfferingRepository.save(serviceOffering);
        return serviceOfferingMapper.toDto(savedService);
    }

    @Override
    @Transactional
    public ServiceOfferingResponseDTO updateServiceOffering(UUID id, UpdateServiceOfferingRequestDTO request, UUID idProveedorFromJWT) {
        ServiceOffering service = serviceOfferingRepository.findById(id)
                .orElseThrow(() -> new ServiceNotFoundException(id));

        // Validate ownership
        if (!service.getIdProveedor().equals(idProveedorFromJWT)) {
            throw new ProviderMismatchException();
        }

        // Update fields
        if (request.getNombreServicio() != null) {
            service.setNombreServicio(request.getNombreServicio());
        }
        if (request.getDuracionMinutos() != null) {
            service.setDuracionMinutos(request.getDuracionMinutos());
        }
        if (request.getPrecio() != null) {
            service.setPrecio(request.getPrecio());
        }
        if (request.getDescripcion() != null) {
            service.setDescripcion(request.getDescripcion());
        }
        if (request.getCapacidadMaxima() != null) {
            service.setCapacidadMaxima(request.getCapacidadMaxima());
        }

        ServiceOffering savedService = serviceOfferingRepository.save(service);
        return serviceOfferingMapper.toDto(savedService);
    }

    @Override
    @Transactional
    public void deleteServiceOffering(UUID id, UUID idProveedorFromJWT) {
        ServiceOffering service = serviceOfferingRepository.findById(id)
                .orElseThrow(() -> new ServiceNotFoundException(id));

        // Validate ownership
        if (!service.getIdProveedor().equals(idProveedorFromJWT)) {
            throw new ProviderMismatchException();
        }

        // Validate not already inactive
        if (!service.getActivo()) {
            throw new ServiceAlreadyInactiveException(id);
        }

        service.setActivo(false);
        serviceOfferingRepository.save(service);
    }

    @Override
    @Transactional
    public void permanentlyDeleteServiceOffering(UUID id) {
        ServiceOffering service = serviceOfferingRepository.findById(id)
                .orElseThrow(() -> new ServiceNotFoundException(id));
        serviceOfferingRepository.delete(service);
    }

    @Override
    public List<ServiceOfferingResponseDTO> getServicesByProvider(UUID idProveedor, UUID idProveedorFromJWT) {
        // Validate that the requesting provider is the same as the one being queried
        if (!idProveedor.equals(idProveedorFromJWT)) {
            throw new ProviderMismatchException();
        }

        return serviceOfferingRepository.findByIdProveedor(idProveedor).stream()
                .map(serviceOfferingMapper::toDto)
                .toList();
    }

    @Override
    public List<ServiceOfferingResponseDTO> getActiveServicesByCategory(UUID idCategoria) {
        // Validate category is active
        CategoryResponseDTO category = categoryService.getCategoryById(idCategoria);
        if (category == null || !Boolean.TRUE.equals(category.getActiva())) {
            throw new CategoryInactiveException(idCategoria);
        }

        // Get all active services and filter by provider's category
        return serviceOfferingRepository.findByActivoTrue().stream()
                .filter(service -> {
                    try {
                        var provider = authClient.getProviderById(service.getIdProveedor());
                        if (provider != null && provider.getBody() != null) {
                            return idCategoria.equals(provider.getBody().getIdCategoria());
                        }
                    } catch (Exception e) {
                        // If provider not found, skip this service
                    }
                    return false;
                })
                .map(serviceOfferingMapper::toDto)
                .toList();
    }

    @Override
    public List<ServiceOfferingResponseDTO> getAllActiveServices() {
        return serviceOfferingRepository.findByActivoTrue().stream()
                .map(serviceOfferingMapper::toDto)
                .toList();
    }
}
