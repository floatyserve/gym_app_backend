package com.example.demo.exceptions;

import com.example.demo.common.ResourceType;
import lombok.Getter;

@Getter
public class ReferenceNotFoundException extends RuntimeException {
    private final ResourceType resource;
    private final String field;

    public ReferenceNotFoundException(ResourceType resource, String field) {
        super(resource.code() + " not found for field " + field);
        this.resource = resource;
        this.field = field;
    }

    public ReferenceNotFoundException(ResourceType resource, String field, String message) {
        super(message);
        this.resource = resource;
        this.field = field;
    }
}
