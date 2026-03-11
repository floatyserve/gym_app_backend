package com.example.demo.card.domain;

import com.example.demo.common.ResourceType;
import com.example.demo.customer.domain.Customer;
import com.example.demo.exceptions.BadRequestException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class AccessCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    private AccessCardStatus status;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    public AccessCard(String code) {
        this.code = code;
        this.status = AccessCardStatus.INACTIVE;
    }

    public boolean isActive() {
        return status == AccessCardStatus.ACTIVE;
    }

    private boolean isReadyToAssign() {
        return isReadyToBeActivated() && customer == null;
    }

    private boolean isReadyToDetach() {
        return status == AccessCardStatus.ACTIVE && customer != null;
    }

    private boolean isReadyToBeActivated() {
        return status == AccessCardStatus.INACTIVE;
    }

    public void activate() {
        if(!isReadyToBeActivated()) {
            throw new IllegalStateException("Only INACTIVE cards can be activated");
        }
        this.status = AccessCardStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = AccessCardStatus.INACTIVE;
    }

    public void revoke() {
        this.status = AccessCardStatus.BLOCKED;
    }

    public void markLost() {
        this.status = AccessCardStatus.LOST;
    }

    public void markDamaged(){
        this.status = AccessCardStatus.DAMAGED;
    }

    public void assign(Customer customer) {
        if (!isReadyToAssign()) {
            throw new BadRequestException(
                    ResourceType.ACCESS_CARD,
                    "customer",
                    "This card is already assigned to another customer"
            );
        }

        customer.getAccessCards().add(this);

        this.customer = customer;
    }

    public void detach() {
        if (!isReadyToDetach()) {
            throw new IllegalStateException("Only active cards with assigned customer can be detached");
        }

        customer.getAccessCards().remove(this);

        this.customer = null;
    }

}
