package com.example.demo.card.repository.specification;

import com.example.demo.card.domain.AccessCard;
import com.example.demo.card.domain.AccessCardStatus;
import com.example.demo.common.specification.BaseSpecification;
import org.springframework.data.jpa.domain.Specification;

public final class AccessCardSpecification extends BaseSpecification {

    private AccessCardSpecification() {}

    public static Specification<AccessCard> build(
            String code,
            AccessCardStatus status,
            Long customerId
    ) {
        Specification<AccessCard> spec = alwaysTrue();

        if (code != null && !code.isBlank()) {
            spec = spec.and(hasCode(code));
        }

        if (status != null) {
            spec = spec.and(hasStatus(status));
        }

        if (customerId != null) {
            spec = spec.and(hasCustomerId(customerId));
        }

        return spec;
    }

    public static Specification<AccessCard> hasCode(String code) {
        return (root, _, cb) ->
                cb.equal(root.get("code"), code);
    }

    public static Specification<AccessCard> hasStatus(AccessCardStatus status) {
        return (root, _, cb) ->
                cb.equal(root.get("status"), status);
    }

    public static Specification<AccessCard> hasCustomerId(Long customerId) {
        return (root, _, cb) ->
                cb.equal(root.get("customer").get("id"), customerId);
    }
}