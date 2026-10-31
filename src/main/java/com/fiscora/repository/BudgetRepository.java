package com.fiscora.repository;

import com.fiscora.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUser_UserIdOrderByCategory_NameAsc(Long userId);

    Optional<Budget> findByBudgetIdAndUser_UserId(Long budgetId, Long userId);

    boolean existsByUser_UserIdAndCategory_CategoryId(Long userId, Long categoryId);

    void deleteByCategory_CategoryId(Long categoryId);
}