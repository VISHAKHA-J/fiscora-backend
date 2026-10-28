package com.fiscora.service;

import com.fiscora.dto.account.AccountResponse;
import com.fiscora.dto.account.CreateAccountRequest;
import com.fiscora.dto.account.UpdateAccountRequest;
import com.fiscora.entity.Account;
import com.fiscora.entity.User;
import com.fiscora.exception.AccountNameAlreadyExistsException;
import com.fiscora.exception.AccountNotFoundException;
import com.fiscora.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fiscora.exception.AccountHasTransactionsException;
import com.fiscora.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountService(AccountRepository accountRepository,
                          TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public AccountResponse create(User user, CreateAccountRequest request) {
        String name = request.name().trim();
        ensureNameIsFree(user, name);

        BigDecimal openingBalance = request.openingBalance() != null
                ? request.openingBalance()
                : BigDecimal.ZERO;

        Account account = Account.builder()
                .user(user)
                .name(name)
                .type(request.type())
                .balance(openingBalance.setScale(2))
                .build();

        return AccountResponse.from(accountRepository.save(account));
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> list(User user) {
        return accountRepository.findByUser_UserIdOrderByCreatedAtAsc(user.getUserId())
                .stream()
                .map(AccountResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public AccountResponse get(User user, Long accountId) {
        return AccountResponse.from(findOwned(user, accountId));
    }

    @Transactional
    public AccountResponse update(User user, Long accountId, UpdateAccountRequest request) {
        Account account = findOwned(user, accountId);
        String name = request.name().trim();

        // Only check for a clash when the name actually changes.
        if (!account.getName().equalsIgnoreCase(name)) {
            ensureNameIsFree(user, name);
        }

        account.setName(name);
        account.setType(request.type());
        return AccountResponse.from(accountRepository.save(account));
    }

    @Transactional
    public void delete(User user, Long accountId) {
        Account account = findOwned(user, accountId);

        long count = transactionRepository.countByAccount_AccountId(accountId);
        if (count > 0) {
            throw new AccountHasTransactionsException(
                    "This account has " + count + (count == 1 ? " transaction" : " transactions")
                            + ", so it can't be deleted. Delete or move them first.");
        }
        accountRepository.delete(account);
    }

    // Returns 404 for both "does not exist" and "belongs to someone else",
    // so the API never reveals which account ids exist.
    private Account findOwned(User user, Long accountId) {
        return accountRepository.findByAccountIdAndUser_UserId(accountId, user.getUserId())
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
    }

    private void ensureNameIsFree(User user, String name) {
        if (accountRepository.existsByUser_UserIdAndNameIgnoreCase(user.getUserId(), name)) {
            throw new AccountNameAlreadyExistsException("You already have an account named \"" + name + "\"");
        }
    }
}