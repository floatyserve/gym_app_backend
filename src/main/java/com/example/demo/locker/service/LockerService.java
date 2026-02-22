package com.example.demo.locker.service;

import com.example.demo.locker.domain.Locker;
import com.example.demo.locker.domain.LockerStats;
import com.example.demo.locker.service.model.LockerSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LockerService {
    Page<Locker> search(LockerSearchCriteria criteria, Pageable pageable);

    Locker findById(Long id);

    Locker findByNumber(Integer number);

    Locker findFirstAvailable();

    Locker create(Integer number);

    void assertAvailable(Locker locker);

    Locker outOfOrder(Locker locker);

    Locker restore(Locker locker);

    LockerStats getLockerStats();
}
