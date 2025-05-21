package com.trustreview.trustreview.Repository;

import com.trustreview.trustreview.Entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Review findReviewById(Long id);
}
