package com.trustreview.trustreview.API;

import com.trustreview.trustreview.Entity.Voucher;
import com.trustreview.trustreview.Model.VoucherBatchUpdateRequest;
import com.trustreview.trustreview.Model.VoucherCreateRequest;
import com.trustreview.trustreview.Service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/vouchers")
public class VoucherController {

    @Autowired
    private VoucherService voucherService;

    @PostMapping("/bulk-create")
    public ResponseEntity<List<Voucher>> createVouchers(@RequestBody VoucherCreateRequest request) {
        return ResponseEntity.ok(
                voucherService.createVouchersFromInput(
                        request.getCodes(),
                        request.getDescription()
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<Voucher>> getAllVouchers() {
        return ResponseEntity.ok(voucherService.getAllVouchers());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVoucher(@PathVariable Long id) {
        voucherService.deleteVoucher(id);
        return ResponseEntity.ok("Xóa voucher thành công");
    }

    @PutMapping("/batch-update")
    public ResponseEntity<String> updateVoucherBatch(@RequestBody VoucherBatchUpdateRequest request) {
        int updatedCount = voucherService.updateBatchDetails(
                request.getBatchCode(),
                request.getDescription(),
                request.getRequiredPoint(),
                request.isActive()
        );
        return ResponseEntity.ok("Updated " + updatedCount + " vouchers for batch " + request.getBatchCode());
    }
}
