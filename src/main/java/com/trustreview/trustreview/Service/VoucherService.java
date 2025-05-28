package com.trustreview.trustreview.Service;

import com.trustreview.trustreview.Entity.Account;
import com.trustreview.trustreview.Entity.Partner;
import com.trustreview.trustreview.Entity.Voucher;
import com.trustreview.trustreview.Repository.VoucherRepository;
import com.trustreview.trustreview.Utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private AccountUtils accountUtils;

    public List<Voucher> createVouchersFromInput(String input, String description, Integer requiredPoint) {
        Account account = accountUtils.getAccountCurrent();
        if (!(account instanceof Partner partner)) {
            throw new BadCredentialsException("Chỉ partner mới được phép tạo voucher");
        }

        String normalized = input.replace("\r", " ").replace("\n", " ");
        String[] tokens = normalized.split("\\s+");

        List<Voucher> createdVouchers = new ArrayList<>();
        for (String code : tokens) {
            code = code.trim();
            if (!code.isEmpty() && !voucherRepository.existsByCode(code)) {
                Voucher voucher = new Voucher();
                voucher.setCode(code);
                voucher.setDescription(description);
                voucher.setRequiredPoint(requiredPoint);
                voucher.setCreatedAt(LocalDateTime.now());
                voucher.setActive(true);
                voucher.setPartnerVoucher(partner);
                createdVouchers.add(voucher);
            }
        }
        return voucherRepository.saveAll(createdVouchers);
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
}
