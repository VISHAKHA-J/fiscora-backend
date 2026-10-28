package com.fiscora.repository;

import com.fiscora.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTransactionIdAndUser_UserId(Long transactionId, Long userId);

    long countByAccount_AccountId(Long accountId);

    long countByCategory_CategoryId(Long categoryId);

    // Every filter is optional: pass null to skip it. Newest first.
    @Query("""
            select t from Transaction t
            where t.user.userId = :userId
              and (:accountId is null or t.account.accountId = :accountId)
              and (:categoryId is null or t.category.categoryId = :categoryId)
              and (:from is null or t.transactionDate >= :from)
              and (:to is null or t.transactionDate <= :to)
            order by t.transactionDate desc, t.transactionId desc
            """)
    List<Transaction> search(@Param("userId") Long userId,
                             @Param("accountId") Long accountId,
                             @Param("categoryId") Long categoryId,
                             @Param("from") LocalDate from,
                             @Param("to") LocalDate to);
}