package com.example.demo.visit.mapper;

import com.example.demo.visit.api.dto.ActiveVisitResponseDto;
import com.example.demo.visit.api.dto.HistoryVisitResponseDto;
import com.example.demo.visit.domain.ActiveVisitView;
import com.example.demo.visit.domain.HistoryVisitView;
import org.mapstruct.Mapper;

import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface VisitMapper {
    HistoryVisitResponseDto toHistoryDto(HistoryVisitView historyVisitView);

    ActiveVisitResponseDto toActiveDto(ActiveVisitView activeVisitView);
}
