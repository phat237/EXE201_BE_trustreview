package com.trustreview.trustreview.Service;

import com.trustreview.trustreview.Entity.*;
import com.trustreview.trustreview.Repository.UserVoucherRepository;
import com.trustreview.trustreview.Repository.VoucherRepository;
import com.trustreview.trustreview.Repository.AuthenticationRepository;
import com.trustreview.trustreview.Utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserVoucherService {

    @Autowired
    private UserVoucherRepository userVoucherRepository;

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private AccountUtils accountUtils;

    public UserVoucher redeemVoucher(Long voucherId) {
        Account account = accountUtils.getAccountCurrent();
        if (!(account instanceof Users user)) {
            throw new BadCredentialsException("Chỉ user mới được phép đổi voucher");
        }

        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new BadCredentialsException("Voucher không tồn tại"));

        boolean alreadyRedeemedInBatch = userVoucherRepository.existsByUserVoucherAndVoucherUser_BatchCode(user, voucher.getBatchCode());

        if (alreadyRedeemedInBatch) {
            throw new BadCredentialsException("Bạn đã đổi voucher trong đợt này rồi");
        }

        if (!voucher.isActive()) {
            throw new BadCredentialsException("Voucher đã bị vô hiệu hóa hoặc hết hạn");
        }

        if (voucher.isHasBeenRedeemed()){
            throw new BadCredentialsException("Voucher này đã được đổi!");
        }

        if (user.getPoint() < voucher.getRequiredPoint()) {
            throw new BadCredentialsException("Bạn không đủ điểm để đổi voucher này");
        }

        if (userVoucherRepository.existsByUserVoucherAndVoucherUser(user, voucher)) {
            throw new BadCredentialsException("Bạn đã đổi voucher này rồi");
        }

        user.setPoint(user.getPoint() - voucher.getRequiredPoint());
        authenticationRepository.save(user);

        voucher.setHasBeenRedeemed(true);
        voucherRepository.save(voucher);

        UserVoucher userVoucher = new UserVoucher();
        userVoucher.setUserVoucher(user);
        userVoucher.setVoucherUser(voucher);
        userVoucher.setRedeemedAt(LocalDateTime.now());

        return userVoucherRepository.save(userVoucher);
    }

    public Page<UserVoucher> getUserVouchersPaging(Pageable pageable) {
        Users user = (Users) accountUtils.getAccountCurrent();
        return userVoucherRepository.findByUserVoucher_Id(user.getId(), pageable);
    }

    public String getVoucherCode(Long userVoucherId) {
        UserVoucher userVoucher = userVoucherRepository.findById(userVoucherId)
                .orElseThrow(() -> new BadCredentialsException("Không tìm thấy dữ liệu đổi voucher"));
        return userVoucher.getVoucherUser().getCode();
    }

}
