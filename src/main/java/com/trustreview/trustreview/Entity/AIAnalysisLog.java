package com.trustreview.trustreview.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    private String analysisResult;

    private boolean isSpam;

    private boolean hasRealExperience;

    @Column(nullable = false, updatable = false)
    private LocalDateTime processedAt;

    @OneToOne
    @JoinColumn(name="review_id", nullable = false, unique = true)
    @JsonIgnore
    Review reviewAIAnalysisLog;
}
