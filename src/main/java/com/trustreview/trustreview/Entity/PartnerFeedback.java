package com.trustreview.trustreview.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter

public class PartnerFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createAt;

    @ManyToOne
    @JoinColumn(name="partner_id", nullable = false)
    @JsonIgnore
    Partner partnerFeedback;

    @OneToOne
    @JoinColumn(name="review_id", nullable = false, unique = true)
    @JsonIgnore
    Review reviewPartnerFeedback;
}
