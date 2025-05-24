package com.trustreview.trustreview.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.trustreview.trustreview.Enums.AIAnalysisResultStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter

public class AIAnalysisLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private AIAnalysisResultStatus analysisResult;

    private boolean isSpam;

    @Column(nullable = false, updatable = false)
    private LocalDateTime processedAt;

    @OneToOne
    @JoinColumn(name="review_id", nullable = false, unique = true)
    @JsonIgnore
    Review reviewAIAnalysisLog;
}
