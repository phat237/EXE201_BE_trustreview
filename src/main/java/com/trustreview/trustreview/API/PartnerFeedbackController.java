package com.trustreview.trustreview.API;

import com.trustreview.trustreview.Entity.PartnerFeedback;
import com.trustreview.trustreview.Service.PartnerFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/partner-feedback")
public class PartnerFeedbackController {

    @Autowired
    private PartnerFeedbackService partnerFeedbackService;

    @PostMapping("/{reviewId}")
    public ResponseEntity<PartnerFeedback> replyToReview(@PathVariable Long reviewId, @RequestBody String content) {
        PartnerFeedback feedback = partnerFeedbackService.replyToReview(reviewId, content);
        return ResponseEntity.ok(feedback);
    }

    @GetMapping
    public ResponseEntity<List<PartnerFeedback>> getAll() {
        return ResponseEntity.ok(partnerFeedbackService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartnerFeedback> getById(@PathVariable Long id) {
        return ResponseEntity.ok(partnerFeedbackService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PartnerFeedback> update(@PathVariable Long id, @RequestBody String newContent) {
        return ResponseEntity.ok(partnerFeedbackService.update(id, newContent));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        partnerFeedbackService.delete(id);
        return ResponseEntity.ok("Đã xóa phản hồi thành công.");
    }

    @GetMapping("/can-feedback")
    public ResponseEntity<Boolean> canPartnerFeedback(@RequestParam Long reviewId) {
        boolean result = partnerFeedbackService.isReviewEligibleForFeedback(reviewId);
        return ResponseEntity.ok(result);
    }
}
