package com.trustreview.trustreview.Service;

import com.trustreview.trustreview.Entity.*;
import com.trustreview.trustreview.Model.AIResponse;
import com.trustreview.trustreview.Model.ReviewRequest;
import com.trustreview.trustreview.Repository.*;
import com.trustreview.trustreview.Utils.AccountUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

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
        if (product == null){
            throw new BadCredentialsException("Đã có sự cố xảy ra, vui lòng thử lại!");
        }
        Account account = accountUtils.getAccountCurrent();
        if (account.getBannedUntil() != null && account.getBannedUntil().isAfter(LocalDateTime.now())) {
            throw new BadCredentialsException("Tài khoản bị tạm khóa đến " + account.getBannedUntil());
        }
        Review existingReview = reviewRepository.findByProductReview_IdAndAccountReview_Id(productId, account.getId());
        if (existingReview != null){
            throw new BadCredentialsException("Bạn đã đánh giá sản phẩm này rồi!");
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

    public Review editReview(Long productId, HttpServletRequest request, ReviewRequest reviewRequest) {
        Account account = accountUtils.getAccountCurrent();
        if (account.getBannedUntil() != null && account.getBannedUntil().isAfter(LocalDateTime.now())) {
            throw new BadCredentialsException("Tài khoản bị tạm khóa đến " + account.getBannedUntil());
        }
        Review existingReview = reviewRepository.findByProductReview_IdAndAccountReview_Id(productId, account.getId());
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
                        existingReview.setReviewHistories(reviewHistory);
                    reviewRepository.save(existingReview);
                    reviewHistoryRepository.save(reviewHistory);
                } else {
                    reviewHistory.setBeforeModifiedAt(LocalDateTime.now());
                    existingReview.setEdited(true);
                    existingReview.setReviewHistories(reviewHistory);
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

    public String deleteAReview(Long reviewId) {
        if (reviewRepository.findReviewById(reviewId) != null) {
            reviewRepository.deleteById(reviewId);
            return "Đã xóa đánh giá thành công!";
        } else {
            return "Đã có lỗi xảy ra, xóa đánh giá không thành công!";
        }
    }

    public ReviewFeedback helpReview(Long reviewId, boolean status) {
        ReviewFeedback reviewFeedback = reviewFeedbackRepository.findByReviewFeedback_IdAndAccountFeedback_Id(reviewId, accountUtils.getAccountCurrent().getId());
        if (reviewFeedback == null){
            Review review = reviewRepository.findReviewById(reviewId);
            ReviewFeedback newFeedback = new ReviewFeedback();
            newFeedback.setReviewFeedback(review);
            newFeedback.setAccountFeedback(accountUtils.getAccountCurrent());
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
}
