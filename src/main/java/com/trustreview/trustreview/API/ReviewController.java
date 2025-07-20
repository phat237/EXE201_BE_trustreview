package com.trustreview.trustreview.API;

import com.trustreview.trustreview.Entity.Review;
import com.trustreview.trustreview.Entity.ReviewFeedback;
import com.trustreview.trustreview.Model.AverageRatingResponse;
import com.trustreview.trustreview.Model.ReviewRequest;
import com.trustreview.trustreview.Model.ReviewStatsResponse;
import com.trustreview.trustreview.Service.ReviewService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/reviews")
@SecurityRequirement(name = "bearerAuth")

public class ReviewController {
    @Autowired
    ReviewService reviewService;

    @PostMapping("/{productId}")
    public ResponseEntity<Review> createdReview(@PathVariable Long productId, HttpServletRequest request, @RequestBody ReviewRequest reviewRequest) {
        Review review = reviewService.addReview(productId, request, reviewRequest);
        return ResponseEntity.ok(review);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Review> updatedReview(@PathVariable Long productId, HttpServletRequest request, @RequestBody ReviewRequest reviewRequest) {
        Review review = reviewService.editReview(productId, request, reviewRequest);
        return ResponseEntity.ok(review);
    }

    @GetMapping("/{productId}/{page}/{size}/paging")
    public ResponseEntity<Page<Review>> getReview(@PathVariable Long productId, int page, int size) {
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(reviewService.getReview(productId, pageable));
    }

    @GetMapping("/review/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable("id") Long reviewId) {
        Review review = reviewService.getReviewById(reviewId);
        return ResponseEntity.ok(review);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.deleteAReview(reviewId));
    }

    @PostMapping("helpful/{reviewId}/{status}")
    public ResponseEntity<ReviewFeedback> feedbackReview(@PathVariable Long reviewId, boolean status) {
        ReviewFeedback reviewFeedback = reviewService.helpReview(reviewId, status);
        return ResponseEntity.ok(reviewFeedback);
    }

    @GetMapping("/helpful/count/{reviewId}")
    public ResponseEntity<Long> countFeedbackByStatus(
            @PathVariable Long reviewId,
            @RequestParam boolean status) {
        long count = reviewService.countFeedbackByReviewIdAndStatus(reviewId, status);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/average-rating/{productId}")
    public ResponseEntity<AverageRatingResponse> getAverageRating(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getAverageRatingAndCount(productId));
    }

    @GetMapping("/stats")
    public ResponseEntity<ReviewStatsResponse> getStatsByAccount() {
        return ResponseEntity.ok(reviewService.getReviewStatsByAccount());
    }

    @GetMapping("/average-rating-by-brand")
    public ResponseEntity<Double> averageRatingByBrand() {
        double avg = reviewService.getAverageRatingByBrand();
        return ResponseEntity.ok(avg);
    }

    @GetMapping("/total-by-brand")
    public ResponseEntity<Long> getTotalReviewsByBrand() {
        long total = reviewService.getTotalReviewsByBrand();
        return ResponseEntity.ok(total);
    }

    @GetMapping("/liked/{reviewId}")
    public ResponseEntity<Boolean> isLikedByCurrentUser(@PathVariable Long reviewId) {
        boolean liked = reviewService.isUserLikedReview(reviewId);
        return ResponseEntity.ok(liked);
    }
}
