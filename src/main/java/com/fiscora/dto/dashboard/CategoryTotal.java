package com.fiscora.dto.dashboard;

import java.math.BigDecimal;

public record CategoryTotal(
        Long categoryId,
        String categoryName,
        BigDecimal amount
) {
}