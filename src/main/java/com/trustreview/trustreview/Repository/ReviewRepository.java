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
            "WHERE r.userReview.id = :userId " +
            "AND r.isVerifiedByAI = true " +
            "AND r.aiAnalysisLog.isSpam = true " +
            "AND DATE(r.createdAt) = :todayDate")
    int countSpamReviewsToday(@Param("userId") Long userId, @Param("todayDate") LocalDate todayDate);

    Review findByProductReview_IdAndUserReview_Id(Long productId, Long accountId);

    @Query("SELECT r FROM Review r " +
            "JOIN r.aiAnalysisLog a " +
            "WHERE r.productReview.id = :productId AND a.isSpam = false")
    Page<Review> findByProductIdAndNotSpam(@Param("productId") Long productId, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r " +
            "JOIN r.aiAnalysisLog a " +
            "WHERE r.productReview.id = :productId AND a.isSpam = false")
    Double findAverageRatingByProductId(@Param("productId") Long productId);

    @Query("SELECT COUNT(r) FROM Review r " +
            "JOIN r.aiAnalysisLog a " +
            "WHERE r.productReview.id = :productId AND a.isSpam = false")
    Long countValidReviewsByProductId(@Param("productId") Long productId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.userReview.id = :accountId")
    long countByAccountId(@Param("accountId") Long accountId);

    @Query("SELECT COUNT(rf) FROM ReviewFeedback rf WHERE rf.userFeedback.id = :accountId AND rf.isHelpful = true")
    Long sumHelpfulCountByAccountId(@Param("accountId") Long accountId);

    @Query("SELECT ROUND(AVG(r.rating), 1) FROM Review r WHERE r.userReview.id = :accountId")
    Double averageRatingByAccountId(@Param("accountId") Long accountId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.userReview.id = :accountId AND r.isVerifiedByAI = true")
    long countVerifiedByAccountId(@Param("accountId") Long accountId);

    @Query("SELECT ROUND(AVG(r.rating), 1) FROM Review r WHERE r.productReview.brandName = :brandName")
    Double averageRatingByBrandName(@Param("brandName") String brandName);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.productReview.brandName = :brandName")
    Long countTotalReviewsByBrand(@Param("brandName") String brandName);

    @Query("SELECT COUNT(r) FROM Review r JOIN r.aiAnalysisLog a WHERE a.isSpam = false")
    long countValidReviews();

    @Query("SELECT r.rating, COUNT(r) FROM Review r JOIN r.aiAnalysisLog a WHERE a.isSpam = false GROUP BY r.rating")
    List<Object[]> countByRating();

    @Query("SELECT COUNT(r) FROM Review r WHERE r.isVerifiedByAI = true")
    long countVerifiedByAI();

    @Query("SELECT COUNT(r) FROM Review r JOIN r.aiAnalysisLog a WHERE a.isSpam = true")
    long countSpamReviews();

    @Query("SELECT COUNT(r) FROM Review r JOIN r.aiAnalysisLog a WHERE a.isSpam = false AND r.createdAt BETWEEN :start AND :end")
    long countValidReviewsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
