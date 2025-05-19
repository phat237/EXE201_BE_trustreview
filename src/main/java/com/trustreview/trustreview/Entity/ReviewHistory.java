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

    @Column(unique = true, nullable = false)
    private String contentOld;

    @Column(nullable = false, updatable = false)
    private LocalDateTime modifiedAt;

    @ManyToOne
    @JoinColumn(name="review_id", nullable = false)
    @JsonIgnore
    Review reviewHistory;
}
