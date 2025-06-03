package com.trustreview.trustreview.API;

import com.trustreview.trustreview.Entity.Account;
import com.trustreview.trustreview.Entity.Partner;
import com.trustreview.trustreview.Entity.PremiumPackage;
import com.trustreview.trustreview.Repository.PartnerRepository;
import com.trustreview.trustreview.Service.PremiumPackageService;
import com.trustreview.trustreview.Service.StripeService;
import com.trustreview.trustreview.Utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/partner")
public class PartnerTopupController {

    @Autowired
    private StripeService stripeService;

    @Autowired
    private AccountUtils accountUtils;

    @PostMapping("/topup/{amount}")
    public ResponseEntity<String> topUp(@PathVariable Long amount) {

        String successUrl = "https://your-frontend-success.com/success";
        String cancelUrl = "https://your-frontend-cancel.com/cancel";

        Account account = accountUtils.getAccountCurrent();
        if (!(account instanceof Partner partner)) {
            throw new BadCredentialsException("Chỉ partner mới được phép nạp tiền vào hệ thống");
        }

        String checkoutUrl = stripeService.createCheckoutSession(partner.getId(), amount, "usd", successUrl, cancelUrl);
        return ResponseEntity.ok(checkoutUrl);
    }

    // 🎯 API xem số dư
    @GetMapping("/balance")
    public ResponseEntity<Double> getBalance() {
        return ResponseEntity.ok(stripeService.getBalace());
    }
}
