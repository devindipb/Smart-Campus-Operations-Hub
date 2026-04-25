package com.sliit.facilitiescatalogue.resource.dto;

import com.sliit.facilitiescatalogue.resource.ResourceStatus;
import com.sliit.facilitiescatalogue.resource.ResourceType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;

public record ResourceRequest(
        @NotBlank(message = "Resource code is required")
        @Size(max = 30, message = "Resource code must not exceed 30 characters")
        String resourceCode,

        @NotBlank(message = "Name is required")
        @Size(max = 120, message = "Name must not exceed 120 characters")
        String name,

        @NotNull(message = "Type is required")
        ResourceType type,

        @NotNull(message = "Capacity is required")
        @Min(value = 0, message = "Capacity must be zero or greater")
        Integer capacity,

        @NotBlank(message = "Location is required")
        @Size(max = 150, message = "Location must not exceed 150 characters")
        String location,

        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description,

        @NotNull(message = "Available from time is required")
        LocalTime availableFrom,

        @NotNull(message = "Available to time is required")
        LocalTime availableTo,

        @NotNull(message = "Status is required")
        ResourceStatus status,

        @NotNull(message = "Active flag is required")
        Boolean active
) {
}

