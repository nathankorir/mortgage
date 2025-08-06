package com.mortgage.backend.enums;

public class Enum {
    public enum ApplicationStatus {
        PENDING,
        UNDER_REVIEW,
        APPROVED,
        REJECTED,
        WITHDRAWN,
        ON_HOLD,
        RESUBMITTED,
        CANCELLED
    }

    public enum DecisionType {
        APPROVED,
        REJECTED
    }

}
