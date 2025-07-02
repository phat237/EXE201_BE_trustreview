package com.trustreview.trustreview.Model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ReviewStatsResponse {
    private long totalReviews;
    private long helpfulCount;
    private double averageRating;
    private long verifiedCount;

    public ReviewStatsResponse(long total, long helpful, double avg, long verified) {
    }
}

