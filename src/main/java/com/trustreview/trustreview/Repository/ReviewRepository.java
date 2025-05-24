package com.trustreview.trustreview.Repository;

import com.trustreview.trustreview.Entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

}
