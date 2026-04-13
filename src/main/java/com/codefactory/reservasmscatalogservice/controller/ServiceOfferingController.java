package com.codefactory.reservasmscatalogservice.controller;

import com.codefactory.reservasmscatalogservice.dto.request.CreateServiceOfferingRequestDTO;
import com.codefactory.reservasmscatalogservice.dto.response.ServiceOfferingResponseDTO;
import com.codefactory.reservasmscatalogservice.service.impl.ServiceOfferingServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/catalog/services")
@RequiredArgsConstructor
public class ServiceOfferingController {

    private final ServiceOfferingServiceImpl serviceOfferingService;

    @PostMapping
    public ResponseEntity<ServiceOfferingResponseDTO> createServiceOffering(
            @Valid @RequestBody CreateServiceOfferingRequestDTO request,
            @RequestParam UUID idProveedor) {
        return new ResponseEntity<>(serviceOfferingService.createServiceOffering(request, idProveedor),
                HttpStatus.CREATED);
    }
}
