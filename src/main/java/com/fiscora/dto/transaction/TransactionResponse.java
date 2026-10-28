package com.fiscora.dto.transaction;

import com.fiscora.entity.Transaction;
import com.fiscora.enums.CategoryType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        BigDecimal amount,
        CategoryType type,
        LocalDate date,
        String note,
        Long accountId,
        String accountName,
        Long categoryId,
        String categoryName,
        LocalDateTime createdAt
) {
    public static TransactionResponse from(Transaction t) {
        return new TransactionResponse(
                t.getTransactionId(),
                t.getAmount(),
                t.getCategory().getType(),
                t.getTransactionDate(),
                t.getNote(),
                t.getAccount().getAccountId(),
                t.getAccount().getName(),
                t.getCategory().getCategoryId(),
                t.getCategory().getName(),
                t.getCreatedAt()
        );
    }
}