package com.example.demo.exceptions;

import com.example.demo.common.ResourceType;
import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException{
    private final ResourceType resource;
    private final String field;
    private final String reason;

    public BadRequestException(ResourceType resource, String field, String reason) {
        super("Bad request for " + resource.code() + " with field " + field + ": " + reason);
        this.resource = resource;
        this.field = field;
        this.reason = reason;
    }
}
