package com.trustreview.trustreview.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter

public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private boolean isVerifiedByAI;

    @Column(length = 1000)
    private String AIComment;

    private String ipAddress;

    private String userAgent;

    private boolean isEdited;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name="product_id", nullable = false)
    @JsonIgnore
    Product productReview;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
//    @JsonIgnore
    Users userReview;

    @OneToMany(mappedBy = "reviewHistory", cascade = CascadeType.ALL)
    @JsonIgnore
    Set<ReviewHistory> reviewHistories;

    @OneToMany(mappedBy = "reviewReport", cascade = CascadeType.ALL)
    @JsonIgnore
    Set<ReviewReport> reviewReports;

    @OneToMany(mappedBy = "reviewFeedback", cascade = CascadeType.ALL)
    @JsonIgnore
    Set<ReviewFeedback> reviewFeedbacks;

    @OneToOne(mappedBy = "reviewAIAnalysisLog", cascade = CascadeType.ALL)
    @JsonIgnore
    AIAnalysisLog aiAnalysisLog;

    @OneToOne(mappedBy = "reviewPartnerFeedback", cascade = CascadeType.ALL)
    @JsonIgnore
    PartnerFeedback partnerFeedback;
}
