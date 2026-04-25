package com.sliit.facilitiescatalogue.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(Long id) {
        super("Resource not found for id: " + id);
    }
}

