package com.codefactory.reservasmscatalogservice.client;

import com.codefactory.reservasmscatalogservice.config.FeignConfig;
import com.codefactory.reservasmscatalogservice.dto.external.ExternalClientDTO;
import com.codefactory.reservasmscatalogservice.dto.external.ExternalProviderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.UUID;

@FeignClient(
        name = "auth-service",
        url = "${services.auth.url}",
        configuration = FeignConfig.class
)
public interface AuthClient {

    @GetMapping("/api/users/clients/{id}")
    ResponseEntity<ExternalClientDTO> getClientById(@PathVariable("id") UUID id);

    @GetMapping("/api/users/providers/{id}")
    ResponseEntity<ExternalProviderDTO> getProviderById(@PathVariable("id") UUID id);
}