package com.trustreview.trustreview.Repository;

import com.trustreview.trustreview.Entity.ReviewHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface ReviewHistoryRepository extends JpaRepository<ReviewHistory, Long> {

}