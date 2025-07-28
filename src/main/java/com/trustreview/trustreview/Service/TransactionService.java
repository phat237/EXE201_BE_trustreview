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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TransactionService {

    @Autowired
    private final TransactionRepository transactionRepository;

    @Autowired
    private final PremiumPackageRepository premiumPackageRepository;

    @Autowired
    private final PartnerRepository partnerRepository;

    @Autowired
    private final PartnerPackageService partnerPackageService;

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

    public void markSuccess(Long orderCode, Long partnerId, Long pakageId) {
        Optional<Transaction> optional = transactionRepository.findByOrderCode(orderCode);
        optional.ifPresent(tx -> {
            tx.setStatus("SUCCESS");
            Partner partner = partnerRepository.findById(partnerId).orElse(null);
            if (partner == null){
                throw new BadCredentialsException("Không tìm thấy người dùng này");
            }
            partner.setMoney(partner.getMoney() + tx.getAmount());
            partnerRepository.save(partner);
            partnerPackageService.purchasePackage(pakageId, partnerId);
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

    public Map<String, Object> getTransactionSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalTransactions", transactionRepository.countAllTransactions());
        Map<String, Long> statusDistribution = new HashMap<>();
        for (Object[] result : transactionRepository.countByStatus()) {
            statusDistribution.put((String) result[0], (Long) result[1]);
        }
        summary.put("statusDistribution", statusDistribution);
        return summary;
    }

    public Map<String, Object> getTransactionRevenueAndGrowth() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startCurrentWeek = now.with(LocalTime.MIN).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDateTime endCurrentWeek = now.with(LocalTime.MAX).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        LocalDateTime startPreviousWeek = startCurrentWeek.minusWeeks(1);
        LocalDateTime endPreviousWeek = endCurrentWeek.minusWeeks(1);
        long currentWeekCount = transactionRepository.countByCreatedAtBetween(startCurrentWeek, endCurrentWeek);
        long previousWeekCount = transactionRepository.countByCreatedAtBetween(startPreviousWeek, endPreviousWeek);
        Long currentWeekRevenue = transactionRepository.sumSuccessAmountBetween(startCurrentWeek, endCurrentWeek);
        Long previousWeekRevenue = transactionRepository.sumSuccessAmountBetween(startPreviousWeek, endPreviousWeek);
        double growthPercentage = previousWeekCount > 0 ? ((double) (currentWeekCount - previousWeekCount) / previousWeekCount) * 100 : (currentWeekCount > 0 ? 100.0 : 0.0);
        return Map.of(
                "currentWeekRevenue", currentWeekRevenue != null ? currentWeekRevenue : 0L,
                "previousWeekRevenue", previousWeekRevenue != null ? previousWeekRevenue : 0L,
                "currentWeekCount", currentWeekCount,
                "previousWeekCount", previousWeekCount,
                "growthPercentage", Double.parseDouble(String.format("%.1f", growthPercentage))
        );
    }

    public List<Map<String, Object>> getTopPartners() {
        Pageable topFive = PageRequest.of(0, 5);
        List<Map<String, Object>> topPartners = new ArrayList<>();
        for (Object[] result : transactionRepository.findTopPartnersBySuccessTransactions(topFive)) {
            Map<String, Object> partner = new HashMap<>();
            partner.put("partnerId", result[0]);
            partner.put("companyName", result[1]);
            partner.put("transactionCount", result[2]);
            topPartners.add(partner);
        }
        return topPartners;
    }
}
