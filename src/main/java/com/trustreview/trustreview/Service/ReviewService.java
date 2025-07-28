package com.trustreview.trustreview.Service;

import com.trustreview.trustreview.Entity.*;
import com.trustreview.trustreview.Model.AIResponse;
import com.trustreview.trustreview.Model.AverageRatingResponse;
import com.trustreview.trustreview.Model.ReviewRequest;
import com.trustreview.trustreview.Model.ReviewStatsResponse;
import com.trustreview.trustreview.Repository.*;
import com.trustreview.trustreview.Utils.AccountUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    ReviewHistoryRepository reviewHistoryRepository;

    @Autowired
    ReviewFeedbackRepository reviewFeedbackRepository;

    @Autowired
    AIAnalysisService aiAnalysisService;

    @Autowired
    ClientService clientService;

    @Autowired
    AccountUtils accountUtils;

    public Review addReview(Long productId, HttpServletRequest request, ReviewRequest reviewRequest) {
        Product product = productRepository.findProductById(productId);
        if (product == null) {
            throw new BadCredentialsException("Sản phẩm không tồn tại!");
        }

        Account account = accountUtils.getAccountCurrent();

        if (!(account instanceof Users user)) {
            throw new BadCredentialsException("Chỉ người dùng mới có thể đánh giá sản phẩm!");
        }

        if (user.getBannedUntil() != null && user.getBannedUntil().isAfter(LocalDateTime.now())) {
            throw new BadCredentialsException("Tài khoản bị tạm khóa đến " + user.getBannedUntil());
        }

//        if (reviewRepository.findByProductReview_IdAndUserReview_Id(productId, user.getId()) != null) {
//            throw new BadCredentialsException("Bạn đã đánh giá sản phẩm này rồi!");
//        }
        if (reviewRepository.findByProductReview_IdAndUserReview_Id(productId, user.getId()) != null &&
                LocalDateTime.now().isBefore(
                        reviewRepository.findByProductReview_IdAndUserReview_Id(productId, user.getId()).getCreatedAt().plusHours(24))
        ) {
            Duration remaining = Duration.between(
                    LocalDateTime.now(),
                    reviewRepository.findByProductReview_IdAndUserReview_Id(productId, user.getId()).getCreatedAt().plusHours(24)
            );
            long hours = remaining.toHours();
            long minutes = remaining.toMinutes() % 60;
            throw new BadCredentialsException("Bạn đã đánh giá sản phẩm này. Vui lòng quay lại sau " +
                    hours + " giờ " + minutes + " phút nữa.");
        }

        AIResponse analysisResult = aiAnalysisService.analyzeText(reviewRequest.getRating(), reviewRequest.getContent());

        Map<String, String> clientInfo = clientService.getClientInfo(request);

        Review review = new Review();
        review.setRating(reviewRequest.getRating());
        review.setContent(reviewRequest.getContent());
        review.setVerifiedByAI(true);
        review.setAIComment(analysisResult.getMessage());
        review.setIpAddress(clientInfo.get("ipAddress"));
        review.setUserAgent(clientInfo.get("userAgent"));
        review.setEdited(false);
        review.setCreatedAt(LocalDateTime.now());
        review.setProductReview(product);
        review.setUserReview(user);

        AIAnalysisLog aiAnalysisLog = new AIAnalysisLog();
        aiAnalysisLog.setReviewAIAnalysisLog(review);
        aiAnalysisLog.setAnalysisResult(analysisResult.getResult());
        aiAnalysisLog.setProcessedAt(LocalDateTime.now());

        boolean isGood = analysisResult.getStatus().contains("GOOD");
        aiAnalysisLog.setSpam(!isGood);

        reviewRepository.save(review);
        aiAnalysisLogRepository.save(aiAnalysisLog);

        if (!isGood) {
            int spamToday = reviewRepository.countSpamReviewsToday(user.getId(), LocalDate.now());

            if (spamToday >= 3) {
                user.setBannedUntil(LocalDateTime.now().plusDays(1));
                authenticationRepository.save(user);
            }

            throw new BadCredentialsException(analysisResult.getMessage());
        }

        return review;
    }

    public Review editReview(Long productId, HttpServletRequest request, ReviewRequest reviewRequest) {
        Account account = accountUtils.getAccountCurrent();
        if (!(account instanceof Users user)) {
            throw new BadCredentialsException("Chỉ người dùng mới có thể sửa đánh giá sản phẩm!");
        }
        if (user.getBannedUntil() != null && user.getBannedUntil().isAfter(LocalDateTime.now())) {
            throw new BadCredentialsException("Tài khoản bị tạm khóa đến " + user.getBannedUntil());
        }
        Review existingReview = reviewRepository.findByProductReview_IdAndUserReview_Id(productId, account.getId());
        if (existingReview != null){
            AIResponse analysisResult = aiAnalysisService.analyzeText(reviewRequest.getRating(), reviewRequest.getContent());
            if (!existingReview.isEdited()){
                ReviewHistory reviewHistory = new ReviewHistory();
                if (analysisResult.getStatus().contains("GOOD")){
                    Map<String, String> clientInfo = clientService.getClientInfo(request);
                        reviewHistory.setContentOld(existingReview.getContent());
                        reviewHistory.setRatingOld(existingReview.getRating());
                        reviewHistory.setBeforeModifiedAt(existingReview.getCreatedAt());

                        existingReview.setRating(reviewRequest.getRating());
                        existingReview.setContent(reviewRequest.getContent());
                        existingReview.setAIComment(analysisResult.getMessage());
                        existingReview.setIpAddress(clientInfo.get("ipAddress"));
                        existingReview.setUserAgent(clientInfo.get("userAgent"));
                        existingReview.setEdited(true);
                        existingReview.setCreatedAt(LocalDateTime.now());
                        reviewHistory.setReviewHistory(existingReview);
                        existingReview.getReviewHistories().add(reviewHistory);
                    reviewRepository.save(existingReview);
                    reviewHistoryRepository.save(reviewHistory);
                } else {
                    reviewHistory.setBeforeModifiedAt(LocalDateTime.now());
                    existingReview.setEdited(true);
                    reviewHistory.setReviewHistory(existingReview);
                    existingReview.getReviewHistories().add(reviewHistory);
                    reviewHistory.setReviewHistory(existingReview);
                    reviewRepository.save(existingReview);
                    throw new BadCredentialsException(analysisResult.getMessage() + ". Vì vậy bạn sẽ không được sửa đánh giá này nữa!");
                }
            } else {
                throw new BadCredentialsException("Bạn đã chỉnh sửa một lần rồi, không thể chỉnh sửa thêm được nữa!");
            }
        } else {
            throw new BadCredentialsException("Không tìm thấy đánh giá trước của bạn, vui lòng thử lại!");
        }
        return existingReview;
    }

    public Page<Review> getReview(Long productId, Pageable pageable) {
        Product product = productRepository.findProductById(productId);
        if (product != null){
            Page<Review> reviews = reviewRepository.findByProductIdAndNotSpam(productId, pageable);
            return reviews;
        } else {
            throw new BadCredentialsException("Đã xảy ra lỗi vui lòng thử lại!");
        }
    }

    public Review getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đánh giá với ID: " + reviewId));
    }


    public String deleteAReview(Long reviewId) {
        if (reviewRepository.findReviewById(reviewId) != null) {
            reviewRepository.deleteById(reviewId);
            return "Đã xóa đánh giá thành công!";
        } else {
            return "Đã có lỗi xảy ra, xóa đánh giá không thành công!";
        }
    }

    public ReviewFeedback helpReview(Long reviewId, boolean status) {
        ReviewFeedback reviewFeedback = reviewFeedbackRepository.findByReviewFeedback_IdAndUserFeedback_Id(reviewId, accountUtils.getAccountCurrent().getId());
        if (reviewFeedback == null){
            Review review = reviewRepository.findReviewById(reviewId);
            ReviewFeedback newFeedback = new ReviewFeedback();
            newFeedback.setReviewFeedback(review);
            newFeedback.setUserFeedback(((Users) accountUtils.getAccountCurrent()));
            newFeedback.setHelpful(status);
            return reviewFeedbackRepository.save(newFeedback);
        } else {
            if (reviewFeedback.isHelpful() != status){
                reviewFeedback.setHelpful(status);
                reviewFeedbackRepository.save(reviewFeedback);
            } else {
                reviewFeedbackRepository.deleteById(reviewFeedback.getId());
            }
        }
        return reviewFeedback;
    }

    public long countFeedbackByReviewIdAndStatus(Long reviewId, boolean status) {
        return reviewFeedbackRepository.countByReviewFeedback_IdAndIsHelpful(reviewId, status);
    }


    public AverageRatingResponse getAverageRatingAndCount(Long productId) {
        Double avg = reviewRepository.findAverageRatingByProductId(productId);
        Long count = reviewRepository.countValidReviewsByProductId(productId);
        if (avg == null) avg = 0.0;

        double rounded = Math.floor(avg * 10) / 10;
        if (avg - rounded >= 0.05) {
            rounded += 0.1;
        }
        double finalAvg = Math.round(rounded * 10) / 10.0;

        return new AverageRatingResponse(finalAvg, count);
    }

    public ReviewStatsResponse getReviewStatsByAccount() {
        Long accountId = accountUtils.getAccountCurrent().getId();

        long total = reviewRepository.countByAccountId(accountId);
        Long helpful = reviewRepository.sumHelpfulCountByAccountId(accountId);
        Double avg = reviewRepository.averageRatingByAccountId(accountId);
        long verified = reviewRepository.countVerifiedByAccountId(accountId);

//        long helpful = (helpfulRaw != null) ? helpfulRaw : 0L;
//        double avg = (avgRaw != null) ? avgRaw : 0.0;

        return new ReviewStatsResponse(total, helpful, avg, verified);
    }


    public double getAverageRatingByBrand() {
        Partner partner = (Partner) accountUtils.getAccountCurrent();
        Double avg = reviewRepository.averageRatingByBrandName(partner.getCompanyName());
        return avg != null ? avg : 0.0;
    }

    public long getTotalReviewsByBrand() {
        Partner partner = (Partner) accountUtils.getAccountCurrent();
        Long count = reviewRepository.countTotalReviewsByBrand(partner.getCompanyName());
        return count != null ? count : 0L;
    }

    public boolean isUserLikedReview(Long reviewId) {
        Long userId = accountUtils.getAccountCurrent().getId();
        return reviewFeedbackRepository.existsByUserFeedback_IdAndReviewFeedback_IdAndIsHelpfulTrue(userId, reviewId);
    }

    public Map<String, Object> getReviewSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalReviews", reviewRepository.countValidReviews());
        Map<Integer, Long> ratingDistribution = new HashMap<>();
        for (Object[] result : reviewRepository.countByRating()) {
            ratingDistribution.put((Integer) result[0], (Long) result[1]);
        }
        summary.put("ratingDistribution", ratingDistribution);
        return summary;
    }

    public Map<String, Long> getReviewVerificationStats() {
        return Map.of(
                "verifiedByAI", reviewRepository.countVerifiedByAI(),
                "spamReviews", reviewRepository.countSpamReviews()
        );
    }

    public Map<String, Object> getNewReviewGrowth() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startCurrentWeek = now.with(LocalTime.MIN).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDateTime endCurrentWeek = now.with(LocalTime.MAX).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        LocalDateTime startPreviousWeek = startCurrentWeek.minusWeeks(1);
        LocalDateTime endPreviousWeek = endCurrentWeek.minusWeeks(1);
        long currentWeekCount = reviewRepository.countValidReviewsBetween(startCurrentWeek, endCurrentWeek);
        long previousWeekCount = reviewRepository.countValidReviewsBetween(startPreviousWeek, endPreviousWeek);
        double growthPercentage = previousWeekCount > 0 ? ((double) (currentWeekCount - previousWeekCount) / previousWeekCount) * 100 : (currentWeekCount > 0 ? 100.0 : 0.0);
        return Map.of(
                "currentWeekCount", currentWeekCount,
                "previousWeekCount", previousWeekCount,
                "growthPercentage", Double.parseDouble(String.format("%.1f", growthPercentage))
        );
    }
}
