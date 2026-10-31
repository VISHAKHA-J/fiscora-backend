package com.fiscora.controller;

import com.fiscora.dto.budget.BudgetResponse;
import com.fiscora.dto.budget.CreateBudgetRequest;
import com.fiscora.dto.budget.UpdateBudgetRequest;
import com.fiscora.entity.User;
import com.fiscora.service.BudgetService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping
    public ResponseEntity<BudgetResponse> create(@AuthenticationPrincipal User user,
                                                 @Valid @RequestBody CreateBudgetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetService.create(user, request));
    }

    // GET /api/budgets  or  GET /api/budgets?month=2026-10
    @GetMapping
    public List<BudgetResponse> list(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        return budgetService.list(user, month);
    }

    @PutMapping("/{id}")
    public BudgetResponse update(@AuthenticationPrincipal User user,
                                 @PathVariable Long id,
                                 @Valid @RequestBody UpdateBudgetRequest request) {
        return budgetService.update(user, id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User user, @PathVariable Long id) {
        budgetService.delete(user, id);
        return ResponseEntity.noContent().build();
    }
}