package com.sliit.facilitiescatalogue.resource.dto;

import com.sliit.facilitiescatalogue.resource.ResourceStatus;
import com.sliit.facilitiescatalogue.resource.ResourceType;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record ResourceResponse(
        Long id,
        String resourceCode,
        String name,
        ResourceType type,
        Integer capacity,
        String location,
        String description,
        LocalTime availableFrom,
        LocalTime availableTo,
        ResourceStatus status,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

