package com.example.demo.locker.repository;

import com.example.demo.locker.domain.Locker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LockerRepository extends
        JpaSpecificationExecutor<Locker>,
        JpaRepository<Locker, Long> {
    Optional<Locker> findByNumber(Integer number);

    boolean existsByNumber(Integer number);

    @Query("""
SELECT COUNT(l) FROM Locker l
WHERE l.status = 'AVAILABLE' AND NOT EXISTS (
    SELECT 1 FROM LockerAssignment la WHERE la.locker = l AND la.releasedAt IS NULL
)
""")
    long countAvailableLockers();

    @Query("""
SELECT COUNT(l) FROM Locker l
WHERE EXISTS (
    SELECT 1 FROM LockerAssignment la WHERE la.locker = l AND la.releasedAt IS NULL
) AND l.status = 'AVAILABLE'
""")
    long countOccupiedLockers();

    @Query("SELECT COUNT(l) FROM Locker l WHERE l.status = 'OUT_OF_ORDER'")
    long countUnavailableLockers();
}
