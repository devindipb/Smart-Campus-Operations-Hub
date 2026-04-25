package com.sliit.facilitiescatalogue.resource.dto;

import com.sliit.facilitiescatalogue.resource.ResourceStatus;
import jakarta.validation.constraints.NotNull;

public record ResourceStatusUpdateRequest(
        @NotNull(message = "Status is required")
        ResourceStatus status
) {
}

