package com.codefactory.reservasmscatalogservice.client;

import com.codefactory.reservasmscatalogservice.dto.external.ExternalClientDTO;
import com.codefactory.reservasmscatalogservice.dto.external.ExternalProviderDTO;
import com.codefactory.reservasmscatalogservice.exception.ExternalServiceException;
import com.codefactory.reservasmscatalogservice.exception.ResourceNotFoundException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthClientWrapper {

    private final AuthClient authClient;

    public ExternalClientDTO getClientOrThrow(UUID clientId) {
        try {
            var response = authClient.getClientById(clientId);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
            throw ExternalServiceException.unavailable("auth-service");
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Cliente no encontrado en servicio de autenticación");
        } catch (FeignException e) {
            throw ExternalServiceException.unavailable("auth-service");
        }
    }

    public ExternalProviderDTO getProviderOrThrow(UUID providerId) {
        try {
            var response = authClient.getProviderById(providerId);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
            throw ExternalServiceException.unavailable("auth-service");
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Proveedor no encontrado en servicio de autenticación");
        } catch (FeignException e) {
            throw ExternalServiceException.unavailable("auth-service");
        }
    }
}