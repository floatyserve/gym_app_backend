package com.example.demo.customer.mapper;

import com.example.demo.customer.api.dto.FrontDeskCheckInDto;
import com.example.demo.customer.domain.Customer;
import com.example.demo.membership.service.model.MembershipAvailability;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FrontDeskMapper {

    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "fullName", source = "customer.fullName")
    @Mapping(target = "email", source = "customer.email")
    @Mapping(target = "activeCardCode", source = "activeCardCode")
    @Mapping(target = "membershipType", source = "availability.membership.type")
    @Mapping(target = "membershipStatus", source = "availability.membership.status")
    @Mapping(target = "membershipDuration", source = "availability.membership.duration")
    @Mapping(target = "visitLimit", source = "availability.membership.visitLimit")
    @Mapping(target = "startsAt", source = "availability.membership.startsAt")
    @Mapping(target = "endsAt", source = "availability.membership.endsAt")
    @Mapping(target = "remainingVisits", source = "availability.remainingVisits")
    FrontDeskCheckInDto toCheckInDto(Customer customer, String activeCardCode, MembershipAvailability availability);
}