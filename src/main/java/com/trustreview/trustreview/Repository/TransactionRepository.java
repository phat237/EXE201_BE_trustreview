package com.trustreview.trustreview.Repository;

import com.trustreview.trustreview.Entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByOrderCode(Long orderCode);
}
