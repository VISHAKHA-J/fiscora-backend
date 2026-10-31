package com.fiscora.enums;

public enum BudgetStatus {
    OK,     // under 80% of the limit
    NEAR,   // 80% or more of the limit
    OVER    // spent more than the limit
}