package com.trustreview.trustreview.Service;

import com.trustreview.trustreview.Entity.*;
import com.trustreview.trustreview.Repository.*;
import com.trustreview.trustreview.Utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PartnerFeedbackService {

    @Autowired
    private PartnerFeedbackRepository partnerFeedbackRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private PartnerPackageRepository partnerPackageRepository;

    @Autowired
    private AccountUtils accountUtils;

    public PartnerFeedback replyToReview(Long reviewId, String content) {
        Account account = accountUtils.getAccountCurrent();
        if (!(account instanceof Partner partner)) {
            throw new BadCredentialsException("Chỉ partner mới có quyền phản hồi đánh giá");
        }

        Review review = reviewRepository.findReviewById(reviewId);
        if (review == null) {
            throw new BadCredentialsException("Đánh giá không tồn tại");
        }

        if (partnerFeedbackRepository.findByReviewPartnerFeedback(review) != null) {
            throw new BadCredentialsException("Đánh giá này đã được phản hồi");
        }

        boolean hasValidPackage = partnerPackageRepository.findByPartnerPackage(partner).stream()
                .anyMatch(p -> p.isActive() && p.getEndDate().isAfter(LocalDateTime.now()));

        if (!hasValidPackage) {
            throw new BadCredentialsException("Partner cần có gói premium hợp lệ để phản hồi đánh giá");
        }

        PartnerFeedback feedback = new PartnerFeedback();
        feedback.setContent(content);
        feedback.setCreateAt(LocalDateTime.now());
        feedback.setPartnerFeedback(partner);
        feedback.setReviewPartnerFeedback(review);

        return partnerFeedbackRepository.save(feedback);
    }

    public List<PartnerFeedback> getAll() {
        return partnerFeedbackRepository.findAll();
    }

    public PartnerFeedback getById(Long id) {
        return partnerFeedbackRepository.findById(id)
                .orElseThrow(() -> new BadCredentialsException("Không tìm thấy phản hồi"));
    }

    public PartnerFeedback update(Long id, String newContent) {
        PartnerFeedback feedback = partnerFeedbackRepository.findById(id)
                .orElseThrow(() -> new BadCredentialsException("Không tìm thấy phản hồi"));
        feedback.setContent(newContent);
        return partnerFeedbackRepository.save(feedback);
    }

    public void delete(Long id) {
        if (!partnerFeedbackRepository.existsById(id)) {
            throw new BadCredentialsException("Không tìm thấy phản hồi để xóa");
        }
        partnerFeedbackRepository.deleteById(id);
    }
}
