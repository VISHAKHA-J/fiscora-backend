package com.fiscora.dto.dashboard;

import com.fiscora.dto.transaction.TransactionResponse;

import java.math.BigDecimal;
import java.util.List;

public record DashboardSummaryResponse(
        String month,                           // "2026-10"
        BigDecimal totalBalance,                // all accounts, right now
        BigDecimal income,                      // for the month
        BigDecimal expense,                     // for the month
        BigDecimal net,                         // income - expense
        List<CategoryTotal> expenseByCategory,  // for the month, biggest first
        List<TransactionResponse> recentTransactions // latest 5 overall
) {
}