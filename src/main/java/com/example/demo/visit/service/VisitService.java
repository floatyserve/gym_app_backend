package com.example.demo.visit.service;

import com.example.demo.card.domain.AccessCard;
import com.example.demo.customer.domain.Customer;
import com.example.demo.staff.domain.Worker;
import com.example.demo.visit.domain.ActiveVisitView;
import com.example.demo.visit.domain.HistoryVisitView;
import com.example.demo.visit.domain.Visit;
import com.example.demo.visit.service.model.VisitSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

public interface VisitService {
    Visit findById(Long id);

    Visit findActiveVisit(Long id);

    Page<ActiveVisitView> findActiveVisitViews(Pageable pageable);

    void checkInByAccessCard(AccessCard accessCard, Worker worker, Instant at);

    void checkInByCustomer(Customer customer, Worker worker, Instant at);

    void checkOut(Long visitId, Instant at);

    Page<HistoryVisitView> search(VisitSearchCriteria criteria, Pageable pageable);
}
