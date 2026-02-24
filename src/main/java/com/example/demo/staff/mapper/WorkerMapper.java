package com.example.demo.staff.mapper;

import com.example.demo.common.mapper.DateTimeMapper;
import com.example.demo.staff.api.dto.DetailedWorkerResponseDto;
import com.example.demo.staff.api.dto.SimpleWorkerResponseDto;
import com.example.demo.staff.domain.Worker;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = DateTimeMapper.class
)
public interface WorkerMapper {
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "role", source = "user.role")
    SimpleWorkerResponseDto toSimpleDto(Worker worker);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "role", source = "user.role")
    @Mapping(target = "active", source = "user.active")
    DetailedWorkerResponseDto toDetailedDto(Worker worker);
}
