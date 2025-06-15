package com.trustreview.trustreview.Repository;

import com.trustreview.trustreview.Entity.ReviewFeedback;
import com.trustreview.trustreview.Entity.ReviewHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface ReviewFeedbackRepository extends JpaRepository<ReviewFeedback, Long> {
    ReviewFeedback findByReviewFeedback_IdAndUserFeedback_Id(Long reviewId, Long accountId);

    long countByReviewFeedback_IdAndIsHelpful(Long reviewId, boolean status);

}