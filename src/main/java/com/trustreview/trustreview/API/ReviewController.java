package com.trustreview.trustreview.API;

import com.trustreview.trustreview.Entity.Review;
import com.trustreview.trustreview.Model.ReviewRequest;
import com.trustreview.trustreview.Service.ReviewService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/reviews")
@SecurityRequirement(name = "bearerAuth")

public class ReviewController {
    @Autowired
    ReviewService reviewService;

    @PostMapping("/{productId}")
    public ResponseEntity createdReview(@PathVariable Long productId, HttpServletRequest request, @RequestBody ReviewRequest reviewRequest) {
        Review review = reviewService.addReview(productId, request, reviewRequest);
        return ResponseEntity.ok(review);
    }
}
