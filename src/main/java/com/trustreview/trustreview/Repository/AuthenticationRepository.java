package com.trustreview.trustreview.Repository;

import com.trustreview.trustreview.Entity.Account;
import com.trustreview.trustreview.Enums.AccountRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository

public interface AuthenticationRepository extends JpaRepository<Account, Long> {
    Account findByUsername(String username);

    Account findAccountById(long userid);

    List<Account> findAll();

    Long countByRole(AccountRoles accountRoles);

    @Query("SELECT COUNT(a) FROM Account a WHERE a.role = :role AND a.createdAt BETWEEN :start AND :end")
    long countByRoleAndCreatedAtBetween(AccountRoles role, LocalDateTime start, LocalDateTime end);
}

