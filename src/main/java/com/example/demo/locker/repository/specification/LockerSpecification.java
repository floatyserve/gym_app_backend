package com.example.demo.locker.repository.specification;

import com.example.demo.common.specification.BaseSpecification;
import com.example.demo.locker.domain.Locker;
import com.example.demo.locker.domain.LockerAssignment;
import com.example.demo.locker.domain.LockerStatus;
import org.springframework.data.jpa.domain.Specification;

public final class LockerSpecification extends BaseSpecification {

    private LockerSpecification() {}

    public static Specification<Locker> build(
            Integer number,
            LockerStatus status,
            Boolean occupied
    ) {
        Specification<Locker> spec = alwaysTrue();

        if (number != null) {
            spec = spec.and(hasNumber(number));
        }

        if (status != null) {
            spec = spec.and(hasStatus(status));
        }

        if (occupied != null) {
            spec = spec.and(isOccupied(occupied));
        }

        return spec;
    }

    public static Specification<Locker> hasNumber(Integer number) {
        return (root, _, cb) ->
                cb.equal(root.get("number"), number);
    }

    public static Specification<Locker> hasStatus(LockerStatus status) {
        return (root, _, cb) ->
                cb.equal(root.get("status"), status);
    }

    public static Specification<Locker> isOccupied(Boolean occupied) {
        return (root, query, cb) -> {
            var subquery = query.subquery(LockerAssignment.class);
            var subRoot = subquery.from(LockerAssignment.class);
            subquery.select(subRoot)
                    .where(
                            cb.equal(subRoot.get("locker"), root),
                            cb.isNull(subRoot.get("releasedAt"))
                    );

            if (occupied) {
                return cb.exists(subquery);
            } else {
                return cb.not(cb.exists(subquery));
            }
        };
    }
}