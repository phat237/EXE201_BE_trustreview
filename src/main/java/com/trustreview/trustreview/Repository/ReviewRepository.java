package com.trustreview.trustreview.Repository;

import com.trustreview.trustreview.Entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Review findReviewById(Long id);

    @Query("SELECT COUNT(r) " +
            "FROM Review r " +
            "WHERE r.accountReview.id = :userId " +
            "AND r.isVerifiedByAI = true " +
            "AND r.aiAnalysisLog.isSpam = true " +
            "AND DATE(r.createdAt) = :todayDate")
    int countSpamReviewsToday(@Param("userId") Long userId, @Param("todayDate") LocalDate todayDate);

    Review findByProductReview_IdAndAccountReview_Id(Long productId, Long accountId);

    @Query("SELECT r FROM Review r " +
            "JOIN r.aiAnalysisLog a " +
            "WHERE r.productReview.id = :productId AND a.isSpam = false")
    Page<Review> findByProductIdAndNotSpam(@Param("productId") Long productId, Pageable pageable);

}
