package com.example.demo.exceptions;

import com.example.demo.common.ResourceType;
import lombok.Getter;

@Getter
public class AlreadyExistsException extends RuntimeException {
    private final ResourceType resource;
    private final String field;

    public AlreadyExistsException(ResourceType resource, String field) {
        super(resource.code() + " already exists for field " + field);
        this.resource = resource;
        this.field = field;
    }

}
