package com.fiscora.service;

import com.fiscora.dto.budget.BudgetResponse;
import com.fiscora.dto.budget.CreateBudgetRequest;
import com.fiscora.dto.budget.UpdateBudgetRequest;
import com.fiscora.dto.dashboard.CategoryTotal;
import com.fiscora.entity.Budget;
import com.fiscora.entity.Category;
import com.fiscora.entity.User;
import com.fiscora.enums.BudgetStatus;
import com.fiscora.enums.CategoryType;
import com.fiscora.exception.BudgetAlreadyExistsException;
import com.fiscora.exception.BudgetNotFoundException;
import com.fiscora.exception.CategoryNotFoundException;
import com.fiscora.exception.InvalidBudgetCategoryException;
import com.fiscora.repository.BudgetRepository;
import com.fiscora.repository.CategoryRepository;
import com.fiscora.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BudgetService {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal NEAR_THRESHOLD = BigDecimal.valueOf(80);

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    public BudgetService(BudgetRepository budgetRepository,
                         CategoryRepository categoryRepository,
                         TransactionRepository transactionRepository) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public BudgetResponse create(User user, CreateBudgetRequest request) {
        Category category = categoryRepository
                .findByCategoryIdAndUser_UserId(request.categoryId(), user.getUserId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        if (category.getType() != CategoryType.EXPENSE) {
            throw new InvalidBudgetCategoryException("Budgets can only be set on expense categories");
        }
        if (budgetRepository.existsByUser_UserIdAndCategory_CategoryId(user.getUserId(), category.getCategoryId())) {
            throw new BudgetAlreadyExistsException(
                    "You already have a budget for \"" + category.getName() + "\"");
        }

        Budget budget = Budget.builder()
                .user(user)
                .category(category)
                .amount(request.amount().setScale(2))
                .build();
        budget = budgetRepository.save(budget);

        YearMonth month = YearMonth.now();
        return toResponse(budget, month, spentByCategory(user, month));
    }

    // month == null means the current month.
    @Transactional(readOnly = true)
    public List<BudgetResponse> list(User user, YearMonth month) {
        YearMonth target = (month != null) ? month : YearMonth.now();
        Map<Long, BigDecimal> spent = spentByCategory(user, target);

        return budgetRepository.findByUser_UserIdOrderByCategory_NameAsc(user.getUserId())
                .stream()
                .map(budget -> toResponse(budget, target, spent))
                .toList();
    }

    @Transactional
    public BudgetResponse update(User user, Long budgetId, UpdateBudgetRequest request) {
        Budget budget = findOwned(user, budgetId);
        budget.setAmount(request.amount().setScale(2));
        budget = budgetRepository.save(budget);

        YearMonth month = YearMonth.now();
        return toResponse(budget, month, spentByCategory(user, month));
    }

    @Transactional
    public void delete(User user, Long budgetId) {
        budgetRepository.delete(findOwned(user, budgetId));
    }

    // 404 for "does not exist" and "belongs to someone else".
    private Budget findOwned(User user, Long budgetId) {
        return budgetRepository.findByBudgetIdAndUser_UserId(budgetId, user.getUserId())
                .orElseThrow(() -> new BudgetNotFoundException("Budget not found"));
    }

    // category id -> total spent in that month. Reuses the dashboard's grouped query.
    private Map<Long, BigDecimal> spentByCategory(User user, YearMonth month) {
        LocalDate from = month.atDay(1);
        LocalDate to = month.atEndOfMonth();

        Map<Long, BigDecimal> result = new HashMap<>();
        for (CategoryTotal total : transactionRepository
                .totalsByCategory(user.getUserId(), CategoryType.EXPENSE, from, to)) {
            result.put(total.categoryId(), total.amount());
        }
        return result;
    }

    private BudgetResponse toResponse(Budget budget, YearMonth month, Map<Long, BigDecimal> spentMap) {
        BigDecimal spent = spentMap
                .getOrDefault(budget.getCategory().getCategoryId(), BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal limit = budget.getAmount();

        BigDecimal percentUsed = spent.multiply(HUNDRED).divide(limit, 1, RoundingMode.HALF_UP);

        BudgetStatus status;
        if (spent.compareTo(limit) > 0) {
            status = BudgetStatus.OVER;
        } else if (percentUsed.compareTo(NEAR_THRESHOLD) >= 0) {
            status = BudgetStatus.NEAR;
        } else {
            status = BudgetStatus.OK;
        }

        return new BudgetResponse(
                budget.getBudgetId(),
                budget.getCategory().getCategoryId(),
                budget.getCategory().getName(),
                limit,
                month.toString(),
                spent,
                limit.subtract(spent),
                percentUsed,
                status
        );
    }
}