package com.fiscora.dto.dashboard;

import java.math.BigDecimal;

public record MonthlyTotal(
        String month,        // "2026-10"
        BigDecimal income,
        BigDecimal expense
) {
}