package com.example.demo.staff.domain;

import com.example.demo.auth.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "workers")
@Getter
@NoArgsConstructor
@ToString(exclude = "user")
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false, updatable = false)
    private Instant hiredAt;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    public Worker(
            String firstName,
            String lastName,
            String phoneNumber,
            LocalDate birthDate,
            Instant hiredAt,
            User user
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.hiredAt = hiredAt;
        this.user = user;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public void updatePersonalInfo(
            String firstName,
            String lastName,
            String phoneNumber,
            LocalDate birthDate
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
    }
}
