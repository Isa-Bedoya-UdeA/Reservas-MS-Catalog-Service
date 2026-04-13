package com.codefactory.reservasmscatalogservice.service.impl;

import com.codefactory.reservasmscatalogservice.dto.request.CreateServiceOfferingRequestDTO;
import com.codefactory.reservasmscatalogservice.dto.response.ServiceOfferingResponseDTO;
import com.codefactory.reservasmscatalogservice.entity.ServiceOffering;
import com.codefactory.reservasmscatalogservice.mapper.ServiceOfferingMapper;
import com.codefactory.reservasmscatalogservice.repository.ServiceOfferingRepository;
import com.codefactory.reservasmscatalogservice.service.ServiceOfferingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServiceOfferingServiceImpl implements ServiceOfferingService {

    private final ServiceOfferingRepository serviceOfferingRepository;
    private final ServiceOfferingMapper serviceOfferingMapper;

    @Override
    @Transactional
    public ServiceOfferingResponseDTO createServiceOffering(CreateServiceOfferingRequestDTO request, UUID idProveedor) {
        ServiceOffering serviceOffering = serviceOfferingMapper.toEntity(request);
        serviceOffering.setIdProveedor(idProveedor);
        ServiceOffering savedService = serviceOfferingRepository.save(serviceOffering);
        return serviceOfferingMapper.toDto(savedService);
    }
}
