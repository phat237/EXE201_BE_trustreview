package com.trustreview.trustreview.API;

import com.trustreview.trustreview.Entity.UserVoucher;
import com.trustreview.trustreview.Service.UserVoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/user-vouchers")
public class UserVoucherController {

    @Autowired
    private UserVoucherService userVoucherService;

    @PostMapping("/{voucherId}")
    public ResponseEntity<UserVoucher> redeemVoucher(@PathVariable Long voucherId) {
        return ResponseEntity.ok(userVoucherService.redeemVoucher(voucherId));
    }

    @GetMapping("/{page}/{size}/paging")
    public ResponseEntity<Page<UserVoucher>> getUserVouchersPaging(@PathVariable int page, @PathVariable int size) {
        Sort sort = Sort.by("redeemedAt").descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(userVoucherService.getUserVouchersPaging(pageable));
    }

    @GetMapping("/code/{userVoucherId}")
    public ResponseEntity<String> getVoucherCode(@PathVariable Long userVoucherId) {
        return ResponseEntity.ok(userVoucherService.getVoucherCode(userVoucherId));
    }

}
