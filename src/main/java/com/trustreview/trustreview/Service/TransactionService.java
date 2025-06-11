package com.trustreview.trustreview.Service;

import com.trustreview.trustreview.Entity.Account;
import com.trustreview.trustreview.Entity.Partner;
import com.trustreview.trustreview.Entity.PremiumPackage;
import com.trustreview.trustreview.Entity.Transaction;
import com.trustreview.trustreview.Enums.TransactionStatus;
import com.trustreview.trustreview.Repository.PartnerRepository;
import com.trustreview.trustreview.Repository.PremiumPackageRepository;
import com.trustreview.trustreview.Repository.TransactionRepository;
import com.trustreview.trustreview.Utils.AccountUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    @Autowired
    private final TransactionRepository transactionRepository;

    @Autowired
    private final PremiumPackageRepository premiumPackageRepository;

    @Autowired
    private final PartnerRepository partnerRepository;

    private final AccountUtils accountUtils;


    @Transactional
    public Transaction createPendingTransaction(Long packageId, Long orderCode) {
        if (packageId == null || orderCode == null) {
            throw new IllegalArgumentException("Gói và mã code không được để trống");
        }
        PremiumPackage premiumPackage = premiumPackageRepository.findById(packageId).orElse(null);

        if (premiumPackage == null){
            throw new BadCredentialsException("Gói này không tồn tại");
        }

        Account account = accountUtils.getAccountCurrent();
        if (!(account instanceof Partner partner)) {
            throw new BadCredentialsException("Chỉ partner mới có thể giao dịch!");
        }

        Transaction transaction = Transaction.builder()
                .partnerTransaction(partner)
                .amount(premiumPackage.getPrice().longValue())
                .orderCode(orderCode)
                .status(TransactionStatus.PENDING.name())
                .build();

        return transactionRepository.save(transaction);
    }

    public void markSuccess(Long orderCode, Long partnerId) {
        Optional<Transaction> optional = transactionRepository.findByOrderCode(orderCode);
        optional.ifPresent(tx -> {
            tx.setStatus("SUCCESS");
            Partner partner = partnerRepository.findById(partnerId).orElse(null);
            if (partner == null){
                throw new BadCredentialsException("Không tìm thấy người dùng này");
            }
            partner.setMoney(partner.getMoney() + tx.getAmount());
            partnerRepository.save(partner);
            transactionRepository.save(tx);
        });
    }

    public void markFailed(Long orderCode) {
        Optional<Transaction> optional = transactionRepository.findByOrderCode(orderCode);
        optional.ifPresent(tx -> {
            tx.setStatus("FAILED");
            transactionRepository.save(tx);
        });
    }

}
