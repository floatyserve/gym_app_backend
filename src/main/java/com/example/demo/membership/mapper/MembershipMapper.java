package com.example.demo.membership.mapper;

import com.example.demo.membership.api.dto.ActiveMembershipDto;
import com.example.demo.membership.api.dto.MembershipResponseDto;
import com.example.demo.membership.domain.Membership;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface MembershipMapper {

    @Mapping(source = "customer.fullName", target = "customerFullName")
    MembershipResponseDto toDto(Membership membership);

    @Mapping(source = "membership.customer.fullName", target = "customerFullName")
    @Mapping(source = "membership.customer.email", target = "customerEmail")
    @Mapping(source = "remainingVisits", target = "remainingVisits")
    ActiveMembershipDto toActiveDto(Membership membership, Integer remainingVisits);
}
