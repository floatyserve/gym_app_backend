package com.example.demo.locker.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Locker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer number;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LockerStatus status;

    public Locker(Integer number, LockerStatus status) {
        this.number = number;
        this.status = status;
    }

    public void markOutOfOrder() {
        if (this.status == LockerStatus.OUT_OF_ORDER) {
            throw new IllegalStateException("Locker already out of order");
        }
        this.status = LockerStatus.OUT_OF_ORDER;
    }

    public void markAvailable() {
        if (this.status != LockerStatus.OUT_OF_ORDER) {
            throw new IllegalStateException("Only out of order lockers can be restored");
        }
        this.status = LockerStatus.AVAILABLE;
    }
}
