package com.fiscora.controller;

import com.fiscora.dto.account.AccountResponse;
import com.fiscora.dto.account.CreateAccountRequest;
import com.fiscora.dto.account.UpdateAccountRequest;
import com.fiscora.entity.User;
import com.fiscora.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> create(@AuthenticationPrincipal User user,
                                                  @Valid @RequestBody CreateAccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.create(user, request));
    }

    @GetMapping
    public List<AccountResponse> list(@AuthenticationPrincipal User user) {
        return accountService.list(user);
    }

    @GetMapping("/{id}")
    public AccountResponse get(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return accountService.get(user, id);
    }

    @PutMapping("/{id}")
    public AccountResponse update(@AuthenticationPrincipal User user,
                                  @PathVariable Long id,
                                  @Valid @RequestBody UpdateAccountRequest request) {
        return accountService.update(user, id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User user, @PathVariable Long id) {
        accountService.delete(user, id);
        return ResponseEntity.noContent().build();
    }
}