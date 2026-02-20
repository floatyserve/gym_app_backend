package com.example.demo.common.specification;

import org.springframework.data.jpa.domain.Specification;

public abstract class BaseSpecification {
    protected static <T> Specification<T> alwaysTrue() {
        return (_, _, cb) -> cb.conjunction();
    }
}
