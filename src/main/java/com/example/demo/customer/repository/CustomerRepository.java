package com.example.demo.customer.repository;

import com.example.demo.customer.api.dto.CustomerResponseDto;
import com.example.demo.customer.domain.Customer;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @EntityGraph(attributePaths = {"accessCards"})
    @NonNull
    @Override
    Optional<Customer> findById(@NonNull Long id);

    Optional<Customer> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    @Query("""
                SELECT new com.example.demo.customer.api.dto.CustomerResponseDto(
                    c.id,
                    c.fullName,
                    c.phoneNumber,
                    c.email,
                    c.createdAt,
                    ac.code
                )
                FROM Customer c
                LEFT JOIN c.accessCards ac
                     ON ac.status = com.example.demo.card.domain.AccessCardStatus.ACTIVE
                WHERE (:fullName IS NULL OR c.fullName ILIKE CONCAT('%', CAST(:fullName AS string), '%'))
                  AND (:email IS NULL OR c.email = :email)
                  AND (:phoneNumber IS NULL OR c.phoneNumber = :phoneNumber)
                  AND (:cardCode IS NULL OR ac.code = :cardCode)
            """)
    Page<CustomerResponseDto> search(
            String fullName,
            String email,
            String phoneNumber,
            String cardCode,
            Pageable pageable
    );
}
