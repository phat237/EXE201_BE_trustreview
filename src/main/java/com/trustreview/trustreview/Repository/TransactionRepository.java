package com.trustreview.trustreview.Repository;

import com.trustreview.trustreview.Entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByOrderCode(Long orderCode);

    @Query("SELECT COUNT(t) FROM Transaction t")
    long countAllTransactions();

    @Query("SELECT t.status, COUNT(t) FROM Transaction t GROUP BY t.status")
    List<Object[]> countByStatus();

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.createdAt BETWEEN :start AND :end")
    long countByCreatedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT t.partnerTransaction.id, t.partnerTransaction.companyName, COUNT(t) as count FROM Transaction t WHERE t.status = 'SUCCESS' GROUP BY t.partnerTransaction.id, t.partnerTransaction.companyName ORDER BY count DESC")
    List<Object[]> findTopPartnersBySuccessTransactions(Pageable pageable);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.status = 'SUCCESS' AND t.createdAt BETWEEN :start AND :end")
    Long sumSuccessAmountBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
