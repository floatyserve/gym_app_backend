package com.example.demo.visit.repository;

import com.example.demo.customer.domain.Customer;
import com.example.demo.visit.domain.ActiveVisitView;
import com.example.demo.visit.domain.HistoryVisitView;
import com.example.demo.visit.domain.Visit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    long countByCustomerAndCheckedInAtBetween(Customer customer, Instant startsAt, Instant endsAt);

    @Query("""
    SELECT
      v.id AS visitId,
      c.fullName AS customerFullName,
      c.email AS customerEmail,
      v.checkedInAt AS checkedInAt,
      l.id AS lockerId,
      l.number AS lockerNumber
    FROM Visit v
    JOIN v.customer c
    LEFT JOIN LockerAssignment la ON la.visit = v AND la.releasedAt IS NULL
    LEFT JOIN la.locker l
    WHERE v.active = true
""")
    Page<ActiveVisitView> findActiveVisitViews(Pageable pageable);

    @Query("""
        SELECT
            v.id AS id,
            c.fullName AS customerFullName,
            c.email AS customerEmail,
            CONCAT(w.firstName, ' ', w.lastName) AS receptionistFullName,
            u.email AS receptionistEmail,
            v.checkedInAt AS checkedInAt,
            v.checkedOutAt AS checkedOutAt,
            l.number as lockerNumber
        FROM Visit v
        JOIN v.customer c
        JOIN v.worker w
        JOIN w.user u
        LEFT JOIN LockerAssignment la ON la.visit = v
            AND la.assignedAt = (SELECT MAX(la2.assignedAt) FROM LockerAssignment la2 WHERE la2.visit = v)
        LEFT JOIN la.locker l
        WHERE
           (:customerEmail IS NULL OR LOWER(c.email) LIKE LOWER(CONCAT('%', CAST(:customerEmail AS string), '%')))
           AND (:receptionistEmail IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', CAST(:receptionistEmail AS string), '%')))
           AND (:active IS NULL OR v.active = :active)
           AND (CAST(:checkedInAfter AS timestamp) IS NULL OR v.checkedInAt >= :checkedInAfter)
           AND (CAST(:checkedInBefore AS timestamp) IS NULL OR v.checkedInAt <= :checkedInBefore)
           AND (CAST(:checkedOutAfter AS timestamp) IS NULL OR v.checkedOutAt >= :checkedOutAfter)
           AND (CAST(:checkedOutBefore AS timestamp) IS NULL OR v.checkedOutAt <= :checkedOutBefore)
           AND (:lockerNumber IS NULL OR l.number = :lockerNumber)
    """)
    Page<HistoryVisitView> search(
            String customerEmail,
            String receptionistEmail,
            Boolean active,
            Instant checkedInBefore,
            Instant checkedInAfter,
            Instant checkedOutBefore,
            Instant checkedOutAfter,
            Integer lockerNumber,
            Pageable pageable
    );

    boolean existsByCustomerAndCheckedOutAtIsNull(Customer customer);
}
