package com.codefactory.reservasmscatalogservice.repository;

import com.codefactory.reservasmscatalogservice.entity.ServiceOffering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ServiceOfferingRepository extends JpaRepository<ServiceOffering, UUID> {
}
