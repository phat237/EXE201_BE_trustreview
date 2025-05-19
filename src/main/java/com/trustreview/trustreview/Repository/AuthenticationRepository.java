package com.trustreview.trustreview.Repository;

import com.trustreview.trustreview.Entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface AuthenticationRepository extends JpaRepository<Account, Long> {
    Account findByUsername(String username);

    Account findAccountById(long userid);

    List<Account> findAll();

}

