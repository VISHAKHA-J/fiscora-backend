package com.fiscora.service;

import com.fiscora.dto.dashboard.CategoryTotal;
import com.fiscora.dto.dashboard.DashboardSummaryResponse;
import com.fiscora.dto.transaction.TransactionResponse;
import com.fiscora.entity.Account;
import com.fiscora.entity.User;
import com.fiscora.enums.CategoryType;
import com.fiscora.repository.AccountRepository;
import com.fiscora.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fiscora.dto.dashboard.MonthlyTotal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.ArrayList;

@Service
public class DashboardService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public DashboardService(AccountRepository accountRepository,
                            TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    // month == null means the current month.
    @Transactional(readOnly = true)
    public DashboardSummaryResponse summary(User user, YearMonth month) {
        YearMonth target = (month != null) ? month : YearMonth.now();
        LocalDate from = target.atDay(1);
        LocalDate to = target.atEndOfMonth();
        Long userId = user.getUserId();

        BigDecimal totalBalance = accountRepository.findByUser_UserIdOrderByCreatedAtAsc(userId)
                .stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal income = transactionRepository.sumByType(userId, CategoryType.INCOME, from, to);
        BigDecimal expense = transactionRepository.sumByType(userId, CategoryType.EXPENSE, from, to);

        List<CategoryTotal> expenseByCategory =
                transactionRepository.totalsByCategory(userId, CategoryType.EXPENSE, from, to);

        List<TransactionResponse> recent = transactionRepository
                .findTop5ByUser_UserIdOrderByTransactionDateDescTransactionIdDesc(userId)
                .stream()
                .map(TransactionResponse::from)
                .toList();

        return new DashboardSummaryResponse(
                target.toString(),
                totalBalance.setScale(2),
                income.setScale(2),
                expense.setScale(2),
                income.subtract(expense).setScale(2),
                expenseByCategory,
                recent
        );
    }

    // Income and spending for each of the last `months` months, oldest first.
    // Months with no transactions come back as zeros so the chart has no gaps.
    @Transactional(readOnly = true)
    public List<MonthlyTotal> trend(User user, int months) {
        int count = Math.max(1, Math.min(12, months));
        YearMonth current = YearMonth.now();
        Long userId = user.getUserId();

        List<MonthlyTotal> result = new ArrayList<>();
        for (int i = count - 1; i >= 0; i--) {
            YearMonth month = current.minusMonths(i);
            LocalDate from = month.atDay(1);
            LocalDate to = month.atEndOfMonth();

            BigDecimal income = transactionRepository.sumByType(userId, CategoryType.INCOME, from, to);
            BigDecimal expense = transactionRepository.sumByType(userId, CategoryType.EXPENSE, from, to);

            result.add(new MonthlyTotal(month.toString(), income.setScale(2), expense.setScale(2)));
        }
        return result;
    }

}