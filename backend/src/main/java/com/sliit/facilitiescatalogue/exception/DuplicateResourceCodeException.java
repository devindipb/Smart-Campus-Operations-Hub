package com.sliit.facilitiescatalogue.exception;

public class DuplicateResourceCodeException extends RuntimeException {
    public DuplicateResourceCodeException(String resourceCode) {
        super("A resource with code '" + resourceCode + "' already exists");
    }
}

