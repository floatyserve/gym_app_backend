package com.example.demo.visit.domain;

import java.time.Instant;

public interface HistoryVisitView {
    Long getId();
    String getCustomerFullName();
    String getCustomerEmail();
    String getReceptionistFullName();
    String getReceptionistEmail();
    Instant getCheckedInAt();
    Instant getCheckedOutAt();
    Integer getLockerNumber();
}