package com.example.demo.common;

public enum ResourceType {
    ACCESS_CARD,
    CUSTOMER,
    WORKER,
    USER,
    VISIT,
    LOCKER,
    MEMBERSHIP;

    public String code() {
        String name = this.name();
        return name.charAt(0) + name.substring(1).toLowerCase().replace('_', ' ');
    }
}