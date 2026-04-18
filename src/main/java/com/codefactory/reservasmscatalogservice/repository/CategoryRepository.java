package com.codefactory.reservasmscatalogservice.repository;

import com.codefactory.reservasmscatalogservice.entity.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<ServiceCategory, UUID> {
    boolean existsByNombreCategoria(String nombreCategoria);
    List<ServiceCategory> findByActivaTrue();
}
