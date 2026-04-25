package com.sliit.facilitiescatalogue.resource;

import com.sliit.facilitiescatalogue.exception.BadRequestException;
import com.sliit.facilitiescatalogue.exception.DuplicateResourceCodeException;
import com.sliit.facilitiescatalogue.exception.ResourceNotFoundException;
import com.sliit.facilitiescatalogue.resource.dto.ResourceRequest;
import com.sliit.facilitiescatalogue.resource.dto.ResourceResponse;
import com.sliit.facilitiescatalogue.resource.dto.ResourceStatusUpdateRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;

    public ResourceService(ResourceRepository resourceRepository, ResourceMapper resourceMapper) {
        this.resourceRepository = resourceRepository;
        this.resourceMapper = resourceMapper;
    }

    @Transactional(readOnly = true)
    public List<ResourceResponse> getAllResources(ResourceType type,
                                                  Integer minCapacity,
                                                  String location,
                                                  ResourceStatus status,
                                                  Boolean active,
                                                  String keyword) {
        Specification<ResourceEntity> specification = Specification
                .where(ResourceSpecifications.hasType(type))
                .and(ResourceSpecifications.hasMinCapacity(minCapacity))
                .and(ResourceSpecifications.hasLocation(location))
                .and(ResourceSpecifications.hasStatus(status))
                .and(ResourceSpecifications.isActive(active))
                .and(ResourceSpecifications.searchKeyword(keyword));

        return resourceRepository.findAll(specification)
                .stream()
                .map(resourceMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ResourceResponse getResourceById(Long id) {
        return resourceMapper.toResponse(findEntityById(id));
    }

    public ResourceResponse createResource(ResourceRequest request) {
        validateAvailabilityWindow(request);
        if (resourceRepository.existsByResourceCodeIgnoreCase(request.resourceCode().trim())) {
            throw new DuplicateResourceCodeException(request.resourceCode().trim());
        }

        ResourceEntity entity = new ResourceEntity();
        resourceMapper.updateEntity(entity, request);
        return resourceMapper.toResponse(resourceRepository.save(entity));
    }

    public ResourceResponse updateResource(Long id, ResourceRequest request) {
        validateAvailabilityWindow(request);
        ResourceEntity entity = findEntityById(id);

        if (resourceRepository.existsByResourceCodeIgnoreCaseAndIdNot(request.resourceCode().trim(), id)) {
            throw new DuplicateResourceCodeException(request.resourceCode().trim());
        }

        resourceMapper.updateEntity(entity, request);
        return resourceMapper.toResponse(resourceRepository.save(entity));
    }

    public ResourceResponse updateStatus(Long id, ResourceStatusUpdateRequest request) {
        ResourceEntity entity = findEntityById(id);
        entity.setStatus(request.status());
        return resourceMapper.toResponse(resourceRepository.save(entity));
    }

    public void deleteResource(Long id) {
        ResourceEntity entity = findEntityById(id);
        resourceRepository.delete(entity);
    }

    private ResourceEntity findEntityById(Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    private void validateAvailabilityWindow(ResourceRequest request) {
        if (!request.availableFrom().isBefore(request.availableTo())) {
            throw new BadRequestException("Available from time must be earlier than available to time");
        }
    }
}

