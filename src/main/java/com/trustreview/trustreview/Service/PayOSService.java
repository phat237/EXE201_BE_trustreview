package com.trustreview.trustreview.Service;

import com.trustreview.trustreview.Entity.Account;
import com.trustreview.trustreview.Entity.Partner;
import com.trustreview.trustreview.Entity.PremiumPackage;
import com.trustreview.trustreview.Entity.Users;
import com.trustreview.trustreview.Repository.PremiumPackageRepository;
import com.trustreview.trustreview.Utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PayOSService {

    private final PayOS payOS;

    @Autowired
    PremiumPackageRepository premiumPackageRepository;

    @Autowired
    AccountUtils accountUtils;

    public CheckoutResponseData createPayment(Long orderCode, Long packageId) throws Exception {
        PremiumPackage premiumPackage = premiumPackageRepository.findById(packageId).orElse(null);
        if (premiumPackage == null){
            throw new BadCredentialsException("Gói này không tồn tại");
        }
        Account account = accountUtils.getAccountCurrent();
        if (!(account instanceof Partner partner)) {
            throw new BadCredentialsException("Chỉ partner mới được thực hiện hành động này");
        }
        ItemData itemData = ItemData.builder()
                .name(premiumPackage.getName())
                .quantity(1)
                .price(premiumPackage.getPrice().intValue())
                .build();

        PaymentData paymentData = PaymentData.builder()
                .orderCode(orderCode)
                .amount(premiumPackage.getPrice().intValue())
                .description(premiumPackage.getName())
                .returnUrl("http://localhost:8080/success?orderCode=" + orderCode + "&partnerId=" + partner.getId())
                .cancelUrl("http://localhost:8080/fail")
                .item(itemData)
                .build();
        return payOS.createPaymentLink(paymentData);
    }
}
