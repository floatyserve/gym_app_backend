package com.example.demo.common.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DateTimeMapper {

    private final Clock clock;

    public LocalDate toLocalDate(Instant instant) {
        if (instant == null) {
            return null;
        }

        return LocalDate.ofInstant(instant, clock.getZone());
    }

    public Instant toInstant(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }

        return localDate.atStartOfDay(clock.getZone()).toInstant();
    }
}