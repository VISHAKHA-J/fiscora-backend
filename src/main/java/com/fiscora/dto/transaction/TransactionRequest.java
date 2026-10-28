package com.fiscora.dto.transaction;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRequest(

        @NotNull(message = "Account is required")
        Long accountId,

        @NotNull(message = "Category is required")
        Long categoryId,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        @Digits(integer = 13, fraction = 2, message = "Amount can have at most 2 decimal places")
        BigDecimal amount,

        @NotNull(message = "Date is required")
        @PastOrPresent(message = "Date cannot be in the future")
        LocalDate date,

        @Size(max = 255, message = "Note must be 255 characters or fewer")
        String note
) {
}