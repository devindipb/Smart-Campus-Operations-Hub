package com.sliit.facilitiescatalogue.resource;

import org.springframework.data.jpa.domain.Specification;

public final class ResourceSpecifications {

    private ResourceSpecifications() {
    }

    public static Specification<ResourceEntity> hasType(ResourceType type) {
        return (root, query, cb) -> type == null ? null : cb.equal(root.get("type"), type);
    }

    public static Specification<ResourceEntity> hasStatus(ResourceStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<ResourceEntity> hasLocation(String location) {
        return (root, query, cb) -> location == null || location.isBlank()
                ? null
                : cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%");
    }

    public static Specification<ResourceEntity> hasMinCapacity(Integer minCapacity) {
        return (root, query, cb) -> minCapacity == null ? null : cb.greaterThanOrEqualTo(root.get("capacity"), minCapacity);
    }

    public static Specification<ResourceEntity> isActive(Boolean active) {
        return (root, query, cb) -> active == null ? null : cb.equal(root.get("active"), active);
    }

    public static Specification<ResourceEntity> searchKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return null;
            }

            String pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("resourceCode")), pattern),
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("location")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }
}

