package com.trustreview.trustreview.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter

public class ReviewHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String contentOld;

    @Column(nullable = true)
    private Integer ratingOld;

    @Column(nullable = false, updatable = false)
    private LocalDateTime beforeModifiedAt;

    @ManyToOne
    @JoinColumn(name="review_id", nullable = false)
    @JsonIgnore
    Review reviewHistory;

}
