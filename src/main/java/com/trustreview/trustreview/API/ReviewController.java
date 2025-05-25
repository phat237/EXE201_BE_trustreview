package com.trustreview.trustreview.API;

import com.trustreview.trustreview.Entity.Review;
import com.trustreview.trustreview.Entity.ReviewFeedback;
import com.trustreview.trustreview.Model.ReviewRequest;
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

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.deleteAReview(reviewId));
    }

    @PostMapping("helpful/{reviewId}/{status}")
    public ResponseEntity<ReviewFeedback> feedbackReview(@PathVariable Long reviewId, boolean status) {
        ReviewFeedback reviewFeedback = reviewService.helpReview(reviewId, status);
        return ResponseEntity.ok(reviewFeedback);
    }

}
