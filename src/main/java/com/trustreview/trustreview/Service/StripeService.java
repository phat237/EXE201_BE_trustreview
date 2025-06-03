package com.trustreview.trustreview.Service;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.trustreview.trustreview.Entity.Account;
import com.trustreview.trustreview.Entity.Partner;
import com.trustreview.trustreview.Repository.PartnerRepository;
import com.trustreview.trustreview.Utils.AccountUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    @Value("${stripe.api.secret}")
    private String secretKey;

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private AccountUtils accountUtils;

    // Tạo session thanh toán Stripe
    public String createCheckoutSession(Long partnerId, long amount, String currency, String successUrl, String cancelUrl) {
        Stripe.apiKey = secretKey;

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setClientReferenceId(partnerId.toString()) // để webhook biết ai nạp tiền
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(currency)
                                                .setUnitAmount(amount) // cents
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Nạp tiền vào tài khoản TrustReview")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        try {
            Session session = Session.create(params);
            System.out.println(session.toString());
            return session.getUrl();
        } catch (Exception e) {
            throw new RuntimeException("Stripe session creation failed", e);
        }
    }

    // Gọi từ webhook khi thanh toán thành công
    @Transactional
    public void handleSuccessfulPayment(Long accountId, Double amount) {
        System.out.println("🔥 Stripe gọi handleSuccessfulPayment với ID: " + accountId + ", amount: " + amount);

        Partner partner = partnerRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Partner với ID: " + accountId));

        Double current = partner.getMoney() != null ? partner.getMoney() : 0.0;
        System.out.println("💰 Số dư hiện tại: " + current);

        partner.setMoney(current + amount);
        partnerRepository.save(partner);
        System.out.println("✅ Cập nhật thành công. Số dư mới: " + partner.getMoney());
    }

    public Double getBalace() {
        Account account = accountUtils.getAccountCurrent();
        if (!(account instanceof Partner partner)) {
            throw new BadCredentialsException("Chỉ partner mới được phép lấy số dư");
        }
        return partner.getMoney();
    }

//    public void handleSuccessfulPayment(Long accountId, Double amount) {
//        Partner partner = partnerRepository.findById(accountId)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy Partner với ID: " + accountId));
//
//        Double current = partner.getMoney() != null ? partner.getMoney() : 0.0;
//        partner.setMoney(current + amount);
//        partnerRepository.save(partner);
//    }
}