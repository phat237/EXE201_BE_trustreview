package com.trustreview.trustreview.Service;

import com.trustreview.trustreview.Entity.Product;
import com.trustreview.trustreview.Entity.Review;
import com.trustreview.trustreview.Model.AIResponse;
import com.trustreview.trustreview.Model.ReviewRequest;
import com.trustreview.trustreview.Repository.ProductRepository;
import com.trustreview.trustreview.Repository.ReviewRepository;
import com.trustreview.trustreview.Utils.AccountUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service

public class ReviewService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    AIAnalysisService aiAnalysisService;

    @Autowired
    AccountUtils accountUtils;

    public Review addReview(Long productId, HttpServletRequest request, ReviewRequest reviewRequest) {
        Product product = productRepository.findProductById(productId);
        if (product == null){
            throw new BadCredentialsException("Đã có sự cố xảy ra, vui lòng thử lại!");
        }
        AIResponse analysisResult = aiAnalysisService.analyzeText(reviewRequest.getRating(), reviewRequest.getContent());
        if (analysisResult.getStatus().contains("GOOD")){
            Review review = new Review();
            review.setRating(reviewRequest.getRating());
            review.setContent(reviewRequest.getContent());
            review.setVerifiedByAI(true);
            review.setAIComment(analysisResult.getMessage());
            review.setIpAddress(getClientIp(request));
            review.setUserAgent("");
            review.setCreatedAt(LocalDateTime.now());
            review.setProductReview(product);
            review.setAccountReview(accountUtils.getAccountCurrent());
            return reviewRepository.save(review);
        } else {
            throw new BadCredentialsException(analysisResult.getMessage());
        }
    }

    public String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        if ("0:0:0:0:0:0:0:1".equals(ipAddress) || "::1".equals(ipAddress)) {
            ipAddress = "127.0.0.1";
        }
        return ipAddress;
    }
}
