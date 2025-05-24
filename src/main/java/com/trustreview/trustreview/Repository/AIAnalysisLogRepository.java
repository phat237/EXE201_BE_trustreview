package com.trustreview.trustreview.Repository;

import com.trustreview.trustreview.Entity.AIAnalysisLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AIAnalysisLogRepository extends JpaRepository<AIAnalysisLog, Long> {

}