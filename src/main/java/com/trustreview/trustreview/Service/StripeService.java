package com.trustreview.trustreview.Service;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.trustreview.trustreview.Entity.Partner;
import com.trustreview.trustreview.Repository.PartnerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    @Value("${stripe.api.secret}")
    private String secretKey;

    @Autowired
    private PartnerRepository partnerRepository;

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
            return session.getUrl();
        } catch (Exception e) {
            throw new RuntimeException("Stripe session creation failed", e);
        }
    }

    // Gọi từ webhook khi thanh toán thành công
    @Transactional
    public void handleSuccessfulPayment(Long accountId, Double amount) {
        Partner partner = partnerRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Partner với ID: " + accountId));

        Double current = partner.getMoney() != null ? partner.getMoney() : 0.0;
        partner.setMoney(current + amount);
        partnerRepository.save(partner);
    }
}