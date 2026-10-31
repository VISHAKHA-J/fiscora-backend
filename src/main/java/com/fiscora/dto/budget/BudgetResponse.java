package com.fiscora.dto.budget;

import com.fiscora.enums.BudgetStatus;

import java.math.BigDecimal;

public record BudgetResponse(
        Long id,
        Long categoryId,
        String categoryName,
        BigDecimal amount,        // the monthly limit
        String month,             // "2026-10", the month spent/remaining refer to
        BigDecimal spent,         // spent in that month
        BigDecimal remaining,     // amount - spent (negative when over)
        BigDecimal percentUsed,   // one decimal place, can go above 100
        BudgetStatus status
) {
}