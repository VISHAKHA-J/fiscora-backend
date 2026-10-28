package com.fiscora.dto.account;

import com.fiscora.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// The balance is deliberately not editable here. It only changes through transactions.
public record UpdateAccountRequest(

        @NotBlank(message = "Account name is required")
        @Size(max = 50, message = "Account name must be 50 characters or fewer")
        String name,

        @NotNull(message = "Account type is required")
        AccountType type
) {
}