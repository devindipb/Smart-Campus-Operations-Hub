package com.sliit.facilitiescatalogue.resource;

import com.sliit.facilitiescatalogue.exception.BadRequestException;
import com.sliit.facilitiescatalogue.exception.DuplicateResourceCodeException;
import com.sliit.facilitiescatalogue.resource.dto.ResourceRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ResourceServiceTest {

    private ResourceRepository resourceRepository;
    private ResourceService resourceService;

    @BeforeEach
    void setUp() {
        resourceRepository = Mockito.mock(ResourceRepository.class);
        resourceService = new ResourceService(resourceRepository, new ResourceMapper());
    }

    @Test
    void shouldThrowWhenAvailabilityWindowIsInvalid() {
        ResourceRequest request = buildRequest("LAB-100");

        ResourceRequest invalidRequest = new ResourceRequest(
                request.resourceCode(),
                request.name(),
                request.type(),
                request.capacity(),
                request.location(),
                request.description(),
                LocalTime.of(17, 0),
                LocalTime.of(9, 0),
                request.status(),
                request.active()
        );

        assertThrows(BadRequestException.class, () -> resourceService.createResource(invalidRequest));
    }

    @Test
    void shouldThrowWhenResourceCodeAlreadyExists() {
        ResourceRequest request = buildRequest("LAB-100");
        Mockito.when(resourceRepository.existsByResourceCodeIgnoreCase("LAB-100")).thenReturn(true);

        assertThrows(DuplicateResourceCodeException.class, () -> resourceService.createResource(request));
    }

    @Test
    void shouldFilterResourcesUsingRepositorySpecification() {
        ResourceEntity entity = new ResourceEntity();
        entity.setId(1L);
        entity.setResourceCode("LAB-200");
        entity.setName("Lab 200");
        entity.setType(ResourceType.LAB);
        entity.setCapacity(40);
        entity.setLocation("Malabe");
        entity.setAvailableFrom(LocalTime.of(8, 0));
        entity.setAvailableTo(LocalTime.of(16, 0));
        entity.setStatus(ResourceStatus.ACTIVE);
        entity.setActive(true);

        Mockito.when(resourceRepository.findAll(ArgumentMatchers.any(org.springframework.data.jpa.domain.Specification.class)))
                .thenReturn(List.of(entity));

        List<?> results = resourceService.getAllResources(ResourceType.LAB, 30, "Malabe", ResourceStatus.ACTIVE, true, "Lab");
        assertEquals(1, results.size());
    }

    @Test
    void shouldUpdateExistingResource() {
        ResourceEntity entity = new ResourceEntity();
        entity.setId(1L);
        entity.setResourceCode("LAB-100");
        entity.setName("Old Name");
        entity.setType(ResourceType.LAB);
        entity.setCapacity(20);
        entity.setLocation("Old");
        entity.setAvailableFrom(LocalTime.of(8, 0));
        entity.setAvailableTo(LocalTime.of(12, 0));
        entity.setStatus(ResourceStatus.ACTIVE);
        entity.setActive(true);

        Mockito.when(resourceRepository.findById(1L)).thenReturn(Optional.of(entity));
        Mockito.when(resourceRepository.existsByResourceCodeIgnoreCaseAndIdNot("LAB-101", 1L)).thenReturn(false);
        Mockito.when(resourceRepository.save(ArgumentMatchers.any(ResourceEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var updated = resourceService.updateResource(1L, buildRequest("LAB-101"));
        assertEquals("LAB-101", updated.resourceCode());
    }

    private ResourceRequest buildRequest(String code) {
        return new ResourceRequest(
                code,
                "Network Lab",
                ResourceType.LAB,
                40,
                "Malabe",
                "Sample resource",
                LocalTime.of(8, 0),
                LocalTime.of(17, 0),
                ResourceStatus.ACTIVE,
                true
        );
    }
}

