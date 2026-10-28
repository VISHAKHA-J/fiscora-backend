package com.fiscora.controller;

import com.fiscora.dto.transaction.TransactionRequest;
import com.fiscora.dto.transaction.TransactionResponse;
import com.fiscora.entity.User;
import com.fiscora.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> create(@AuthenticationPrincipal User user,
                                                      @Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.create(user, request));
    }

    // All filters are optional, for example:
    // GET /api/transactions?accountId=1&from=2026-10-01&to=2026-10-31
    @GetMapping
    public List<TransactionResponse> list(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Long accountId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return transactionService.list(user, accountId, categoryId, from, to);
    }

    @GetMapping("/{id}")
    public TransactionResponse get(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return transactionService.get(user, id);
    }

    @PutMapping("/{id}")
    public TransactionResponse update(@AuthenticationPrincipal User user,
                                      @PathVariable Long id,
                                      @Valid @RequestBody TransactionRequest request) {
        return transactionService.update(user, id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User user, @PathVariable Long id) {
        transactionService.delete(user, id);
        return ResponseEntity.noContent().build();
    }
}
