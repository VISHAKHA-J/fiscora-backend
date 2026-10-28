package com.fiscora.service;

import com.fiscora.dto.transaction.TransactionRequest;
import com.fiscora.dto.transaction.TransactionResponse;
import com.fiscora.entity.Account;
import com.fiscora.entity.Category;
import com.fiscora.entity.Transaction;
import com.fiscora.entity.User;
import com.fiscora.enums.CategoryType;
import com.fiscora.exception.AccountNotFoundException;
import com.fiscora.exception.CategoryNotFoundException;
import com.fiscora.exception.TransactionNotFoundException;
import com.fiscora.repository.AccountRepository;
import com.fiscora.repository.CategoryRepository;
import com.fiscora.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public TransactionResponse create(User user, TransactionRequest request) {
        Account account = findOwnedAccount(user, request.accountId());
        Category category = findOwnedCategory(user, request.categoryId());
        BigDecimal amount = request.amount().setScale(2);

        Transaction transaction = Transaction.builder()
                .user(user)
                .account(account)
                .category(category)
                .amount(amount)
                .transactionDate(request.date())
                .note(cleanNote(request.note()))
                .build();

        applyToBalance(account, category.getType(), amount);
        return TransactionResponse.from(transactionRepository.save(transaction));
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> list(User user, Long accountId, Long categoryId,
                                          LocalDate from, LocalDate to) {
        return transactionRepository.search(user.getUserId(), accountId, categoryId, from, to)
                .stream()
                .map(TransactionResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TransactionResponse get(User user, Long transactionId) {
        return TransactionResponse.from(findOwned(user, transactionId));
    }

    @Transactional
    public TransactionResponse update(User user, Long transactionId, TransactionRequest request) {
        Transaction transaction = findOwned(user, transactionId);
        Account newAccount = findOwnedAccount(user, request.accountId());
        Category newCategory = findOwnedCategory(user, request.categoryId());
        BigDecimal newAmount = request.amount().setScale(2);

        // Undo the old effect on the old account, then apply the new one.
        // Both can be the same account; that works because they are the same entity.
        undoFromBalance(transaction.getAccount(), transaction.getCategory().getType(), transaction.getAmount());
        applyToBalance(newAccount, newCategory.getType(), newAmount);

        transaction.setAccount(newAccount);
        transaction.setCategory(newCategory);
        transaction.setAmount(newAmount);
        transaction.setTransactionDate(request.date());
        transaction.setNote(cleanNote(request.note()));

        return TransactionResponse.from(transactionRepository.save(transaction));
    }

    @Transactional
    public void delete(User user, Long transactionId) {
        Transaction transaction = findOwned(user, transactionId);
        undoFromBalance(transaction.getAccount(), transaction.getCategory().getType(), transaction.getAmount());
        transactionRepository.delete(transaction);
    }

    // Income adds to the balance, expense subtracts.
    private void applyToBalance(Account account, CategoryType type, BigDecimal amount) {
        BigDecimal delta = (type == CategoryType.INCOME) ? amount : amount.negate();
        account.setBalance(account.getBalance().add(delta));
    }

    private void undoFromBalance(Account account, CategoryType type, BigDecimal amount) {
        BigDecimal delta = (type == CategoryType.INCOME) ? amount : amount.negate();
        account.setBalance(account.getBalance().subtract(delta));
    }

    private String cleanNote(String note) {
        if (note == null || note.isBlank()) {
            return null;
        }
        return note.trim();
    }

    // 404 for "does not exist" and "belongs to someone else".
    private Transaction findOwned(User user, Long transactionId) {
        return transactionRepository.findByTransactionIdAndUser_UserId(transactionId, user.getUserId())
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));
    }

    private Account findOwnedAccount(User user, Long accountId) {
        return accountRepository.findByAccountIdAndUser_UserId(accountId, user.getUserId())
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
    }

    private Category findOwnedCategory(User user, Long categoryId) {
        return categoryRepository.findByCategoryIdAndUser_UserId(categoryId, user.getUserId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
    }
}