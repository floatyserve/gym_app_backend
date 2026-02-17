package com.example.demo.card.repository.specification;

import com.example.demo.card.domain.AccessCard;
import com.example.demo.card.domain.AccessCardStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class AccessCardSpecification {
    public static Specification<AccessCard> build(
            String code,
            AccessCardStatus status,
            Long customerId
    ){
        List<Specification<AccessCard>> specs = new ArrayList<>();

        if (code != null && !code.isBlank()) {
            specs.add((root, _, cb) ->
                    cb.equal(root.get("code"), code));
        }

        if (status != null) {
            specs.add((root, _, cb) ->
                    cb.equal(root.get("status"), status));
        }

        if (customerId != null) {
            specs.add((root, _, cb) ->
                    cb.equal(root.get("customer").get("id"), customerId));
        }

        return specs.stream()
                .reduce(Specification::and)
                .orElse((_, _, cb) -> cb.conjunction());
    }
}
