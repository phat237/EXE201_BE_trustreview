package com.trustreview.trustreview.Model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class ReviewStatsResponse {
    private long totalReviews;
    private long helpfulCount;
    private double averageRating;
    private long verifiedCount;
}

