package com.trustreview.trustreview.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter

public class ReviewFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean isHelpful;

    @ManyToOne
    @JoinColumn(name="account_id", nullable = false)
    @JsonIgnore
    Account accountFeedback;

    @ManyToOne
    @JoinColumn(name="review_id", nullable = false)
    @JsonIgnore
    Review reviewFeedback;
}
