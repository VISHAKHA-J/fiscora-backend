package com.fiscora.repository;

import com.fiscora.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    // Every lookup is scoped to the owner, so one user can never reach another user's account.
    List<Account> findByUser_UserIdOrderByCreatedAtAsc(Long userId);

    Optional<Account> findByAccountIdAndUser_UserId(Long accountId, Long userId);

    boolean existsByUser_UserIdAndNameIgnoreCase(Long userId, String name);
}