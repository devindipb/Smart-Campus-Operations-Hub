package com.sliit.facilitiescatalogue.resource;

import com.sliit.facilitiescatalogue.resource.dto.ResourceRequest;
import com.sliit.facilitiescatalogue.resource.dto.ResourceResponse;
import org.springframework.stereotype.Component;

@Component
public class ResourceMapper {

    public ResourceResponse toResponse(ResourceEntity entity) {
        return new ResourceResponse(
                entity.getId(),
                entity.getResourceCode(),
                entity.getName(),
                entity.getType(),
                entity.getCapacity(),
                entity.getLocation(),
                entity.getDescription(),
                entity.getAvailableFrom(),
                entity.getAvailableTo(),
                entity.getStatus(),
                entity.getActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public void updateEntity(ResourceEntity entity, ResourceRequest request) {
        entity.setResourceCode(request.resourceCode().trim());
        entity.setName(request.name().trim());
        entity.setType(request.type());
        entity.setCapacity(request.capacity());
        entity.setLocation(request.location().trim());
        entity.setDescription(request.description() == null ? null : request.description().trim());
        entity.setAvailableFrom(request.availableFrom());
        entity.setAvailableTo(request.availableTo());
        entity.setStatus(request.status());
        entity.setActive(request.active());
    }
}

