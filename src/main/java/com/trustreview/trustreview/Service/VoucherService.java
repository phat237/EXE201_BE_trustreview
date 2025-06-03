package com.trustreview.trustreview.Service;

import com.trustreview.trustreview.Entity.Account;
import com.trustreview.trustreview.Entity.Partner;
import com.trustreview.trustreview.Entity.Voucher;
import com.trustreview.trustreview.Repository.VoucherRepository;
import com.trustreview.trustreview.Utils.AccountUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private AccountUtils accountUtils;

//    public List<Voucher> createVouchersFromInput(String input, String description) {
//        Account account = accountUtils.getAccountCurrent();
//        if (!(account instanceof Partner partner)) {
//            throw new BadCredentialsException("Chỉ partner mới được phép tạo voucher");
//        }
//
//        String normalized = input.replace("\r", " ").replace("\n", " ");
//        String[] tokens = normalized.split("\\s+");
//
//        List<Voucher> createdVouchers = new ArrayList<>();
//        for (String code : tokens) {
//            code = code.trim();
//            if (!code.isEmpty() && !voucherRepository.existsByCode(code)) {
//                Voucher voucher = new Voucher();
//                voucher.setCode(code);
//                voucher.setDescription(description);
//                voucher.setCreatedAt(LocalDateTime.now());
//                voucher.setBatchCode(generateBatchCode((Partner) account));
//                voucher.setActive(false);
//                voucher.setPartnerVoucher(partner);
//                createdVouchers.add(voucher);
//            }
//        }
//        return voucherRepository.saveAll(createdVouchers);
//    }
//
//    public String generateBatchCode(Partner partner) {
//        String partnerCode = partner.getCompanyName().toUpperCase().replaceAll("\\s+", "");
//        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
//        String prefix = partnerCode + "-" + datePart;
//
//        Integer currentMax = voucherRepository.findMaxBatchNumberToday(partner.getId(), prefix);
//        int nextNo = currentMax != null ? currentMax + 1 : 1;
//        return prefix + "-" + String.format("%03d", nextNo);
//    }

    public List<Voucher> createVouchersFromInput(String input, String description) {
        Account account = accountUtils.getAccountCurrent();
        if (!(account instanceof Partner partner)) {
            throw new BadCredentialsException("Chỉ partner mới được phép tạo voucher");
        }

        // Làm sạch dữ liệu đầu vào
        String[] codes = input.replace("\r", " ")
                .replace("\n", " ")
                .trim()
                .split("\\s+");

        // Sinh batchCode duy nhất cho cả lô
        String batchCode = generateBatchCode(partner);

        List<Voucher> createdVouchers = new ArrayList<>();
        for (String code : codes) {
            if (!code.isEmpty() && !voucherRepository.existsByCode(code)) {
                Voucher voucher = new Voucher();
                voucher.setCode(code);
                voucher.setDescription(description);
                voucher.setCreatedAt(LocalDateTime.now());
                voucher.setBatchCode(batchCode);
                voucher.setActive(false);
                voucher.setHasBeenRedeemed(false);
                voucher.setPartnerVoucher(partner);
                createdVouchers.add(voucher);
            }
        }

        return voucherRepository.saveAll(createdVouchers);
    }

    public String generateBatchCode(Partner partner) {
        String partnerCode = partner.getCompanyName().toUpperCase().replaceAll("\\s+", "");
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = partnerCode + "-" + datePart;

        // Tìm số lớn nhất trong ngày cho partner này
        Integer currentMax = voucherRepository.findMaxBatchNumberToday(partner.getId(), prefix);
        int nextNo = currentMax != null ? currentMax + 1 : 1;

        return prefix + "-" + String.format("%03d", nextNo);
    }


    public List<Voucher> getAllVouchers() {
        return voucherRepository.findAll();
    }

    public void deleteVoucher(Long id) {
        if (!voucherRepository.existsById(id)) {
            throw new BadCredentialsException("Không tìm thấy voucher để xóa");
        }
        voucherRepository.deleteById(id);
    }

    @Transactional
    public int updateBatchDetails(String batchCode, String description, Integer requiredPoint, boolean isActive) {
        return voucherRepository.updateVoucherBatchDetails(batchCode, description, requiredPoint, isActive);
    }
}
