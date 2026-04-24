package com.codefactory.reservasmscatalogservice.repository;

import com.codefactory.reservasmscatalogservice.entity.ServiceOffering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServiceOfferingRepository extends JpaRepository<ServiceOffering, UUID> {

    List<ServiceOffering> findByActivoTrue();

    List<ServiceOffering> findByIdProveedorAndActivoTrue(UUID idProveedor);

    List<ServiceOffering> findByIdProveedor(UUID idProveedor);
}
