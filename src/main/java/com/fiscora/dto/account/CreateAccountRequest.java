package com.fiscora.dto.account;

import com.fiscora.enums.AccountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateAccountRequest(

        @NotBlank(message = "Account name is required")
        @Size(max = 50, message = "Account name must be 50 characters or fewer")
        String name,

        @NotNull(message = "Account type is required")
        AccountType type,

        // Optional. Leave it out to start at 0.
        @DecimalMin(value = "0.00", message = "Opening balance cannot be negative")
        @Digits(integer = 13, fraction = 2, message = "Opening balance can have at most 2 decimal places")
        BigDecimal openingBalance
) {
}