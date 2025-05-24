package com.trustreview.trustreview.Service;

import com.trustreview.trustreview.Entity.AIAnalysisLog;
import com.trustreview.trustreview.Entity.Account;
import com.trustreview.trustreview.Entity.Product;
import com.trustreview.trustreview.Entity.Review;
import com.trustreview.trustreview.Enums.AIAnalysisResultStatus;
import com.trustreview.trustreview.Model.AIResponse;
import com.trustreview.trustreview.Model.ReviewRequest;
import com.trustreview.trustreview.Repository.AIAnalysisLogRepository;
import com.trustreview.trustreview.Repository.AuthenticationRepository;
import com.trustreview.trustreview.Repository.ProductRepository;
import com.trustreview.trustreview.Repository.ReviewRepository;
import com.trustreview.trustreview.Utils.AccountUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service

public class ReviewService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    AIAnalysisLogRepository aiAnalysisLogRepository;

    @Autowired
    AuthenticationRepository authenticationRepository;

    @Autowired
    AIAnalysisService aiAnalysisService;

    @Autowired
    AccountUtils accountUtils;

    public Review addReview(Long productId, HttpServletRequest request, ReviewRequest reviewRequest) {
        Product product = productRepository.findProductById(productId);
        if (product == null){
            throw new BadCredentialsException("Đã có sự cố xảy ra, vui lòng thử lại!");
        }
        Account account = accountUtils.getAccountCurrent();
        if (account.getBannedUntil() != null && account.getBannedUntil().isAfter(LocalDateTime.now())) {
            throw new BadCredentialsException("Tài khoản bị tạm khóa đến " + account.getBannedUntil());
        }
        AIResponse analysisResult = aiAnalysisService.analyzeText(reviewRequest.getRating(), reviewRequest.getContent());
        Review review = new Review();
            review.setRating(reviewRequest.getRating());
            review.setContent(reviewRequest.getContent());
            review.setVerifiedByAI(true);
            review.setAIComment(analysisResult.getMessage());
            review.setIpAddress(getClientIp(request));
            review.setUserAgent("");
            review.setCreatedAt(LocalDateTime.now());
            review.setProductReview(product);
            review.setAccountReview(account);

        AIAnalysisLog aiAnalysisLog = new AIAnalysisLog();
            aiAnalysisLog.setReviewAIAnalysisLog(review);
            aiAnalysisLog.setAnalysisResult(analysisResult.getResult());
            aiAnalysisLog.setProcessedAt(LocalDateTime.now());

        if (analysisResult.getStatus().contains("GOOD")){
            aiAnalysisLog.setSpam(false);
            reviewRepository.save(review);
            aiAnalysisLogRepository.save(aiAnalysisLog);
            return review;
        } else {
            int spamToday = reviewRepository.countSpamReviewsToday(account.getId(), LocalDate.now());
            aiAnalysisLog.setSpam(true);
            reviewRepository.save(review);
            aiAnalysisLogRepository.save(aiAnalysisLog);
            if (spamToday >= 3) {
                account.setBannedUntil(LocalDateTime.now().plusDays(1));
                authenticationRepository.save(account);
            }
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
