package com.trustreview.trustreview.Repository;

import com.trustreview.trustreview.Entity.PartnerFeedback;
import com.trustreview.trustreview.Entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartnerFeedbackRepository extends JpaRepository<PartnerFeedback, Long> {
    PartnerFeedback findByReviewPartnerFeedback(Review review);

    List<PartnerFeedback> findByReviewPartnerFeedback_Id(Long reviewId);

}
