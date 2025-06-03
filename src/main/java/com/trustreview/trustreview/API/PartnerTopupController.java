package com.trustreview.trustreview.API;

import com.trustreview.trustreview.Entity.Partner;
import com.trustreview.trustreview.Entity.PremiumPackage;
import com.trustreview.trustreview.Repository.PartnerRepository;
import com.trustreview.trustreview.Service.PremiumPackageService;
import com.trustreview.trustreview.Service.StripeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/partner")
public class PartnerTopupController {

    @Autowired
    private StripeService stripeService;

    @Autowired
    private PartnerRepository partnerRepository;

    // 🎯 API tạo session thanh toán
    @PostMapping("/topup")
    public ResponseEntity<String> topUp(@RequestParam Long partnerId,
                                        @RequestParam Long amount) {
        // Stripe yêu cầu đơn vị là cents → 1000 = 10.00 USD
        String successUrl = "https://your-frontend.com/success";  // sửa theo frontend thật
        String cancelUrl = "https://your-frontend.com/cancel";

        String checkoutUrl = stripeService.createCheckoutSession(partnerId, amount, "usd", successUrl, cancelUrl);
        return ResponseEntity.ok(checkoutUrl);
    }

    // 🎯 API xem số dư
    @GetMapping("/balance")
    public ResponseEntity<Double> getBalance(@RequestParam Long partnerId) {
        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Partner"));

        return ResponseEntity.ok(partner.getMoney() != null ? partner.getMoney() : 0.0);
    }
}
