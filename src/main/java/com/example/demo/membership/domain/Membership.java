package com.example.demo.membership.domain;

import com.example.demo.customer.domain.Customer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@NoArgsConstructor
@Getter
public class Membership {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    private MembershipType type;

    @Enumerated(EnumType.STRING)
    private MembershipDuration duration;

    private Integer visitLimit;

    private Instant startsAt;
    private Instant endsAt;

    @Enumerated(EnumType.STRING)
    private MembershipStatus status;

    public Membership(
            Customer customer,
            MembershipType type,
            MembershipDuration period,
            Integer visitLimit
    ){
        this.customer = customer;
        this.type = type;
        this.duration = period;
        this.visitLimit = visitLimit;
        this.status = MembershipStatus.PENDING;
    }

    public boolean isLimited() {
        return type == MembershipType.LIMITED;
    }

    public void finishIfExpired(Instant now) {
        if (status == MembershipStatus.ACTIVE && endsAt.isBefore(now)) {
            forceFinish(now);
        }
    }

    public void activate(Instant now) {
        this.startsAt = now;
        this.endsAt = duration.addTo(now);
        this.status = MembershipStatus.ACTIVE;
    }

    public void forceFinish(Instant now) {
        if (status != MembershipStatus.ACTIVE) {
            throw new IllegalStateException("Only active memberships can be finished");
        }
        this.endsAt = now;
        this.status = MembershipStatus.FINISHED;
    }

    public void cancel() {
        if (status == MembershipStatus.FINISHED) {
            throw new IllegalStateException("Finished membership cannot be cancelled");
        }
        this.status = MembershipStatus.CANCELLED;
    }

}

