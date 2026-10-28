package com.fiscora.dto.account;

import com.fiscora.entity.Account;
import com.fiscora.enums.AccountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponse(
        Long id,
        String name,
        AccountType type,
        BigDecimal balance,
        LocalDateTime createdAt
) {

    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getAccountId(),
                account.getName(),
                account.getType(),
                account.getBalance(),
                account.getCreatedAt()
        );
    }
}