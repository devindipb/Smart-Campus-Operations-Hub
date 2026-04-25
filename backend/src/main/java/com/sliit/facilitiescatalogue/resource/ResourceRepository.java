package com.sliit.facilitiescatalogue.resource;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ResourceRepository extends JpaRepository<ResourceEntity, Long>, JpaSpecificationExecutor<ResourceEntity> {
    boolean existsByResourceCodeIgnoreCase(String resourceCode);
    boolean existsByResourceCodeIgnoreCaseAndIdNot(String resourceCode, Long id);
    Optional<ResourceEntity> findByResourceCodeIgnoreCase(String resourceCode);
}

