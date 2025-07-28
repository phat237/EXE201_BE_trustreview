package com.trustreview.trustreview.API;

import com.trustreview.trustreview.Entity.Transaction;
import com.trustreview.trustreview.Model.DepositRequest;
import com.trustreview.trustreview.Service.PayOSService;
import com.trustreview.trustreview.Service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.payos.type.CheckoutResponseData;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    private final PayOSService payOSService;

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestBody DepositRequest request) throws Exception {
        // Tạo mã orderCode duy nhất
        long orderCode = System.currentTimeMillis() / 1000;

        // Tạo transaction PENDING trong DB
        Transaction transaction = transactionService.createPendingTransaction(request.getPackageId(), orderCode);

        // Gọi PayOS để lấy link thanh toán
        CheckoutResponseData checkoutLink = payOSService.createPayment(orderCode, request.getPackageId());

        // Trả link về cho frontend
        return ResponseEntity.ok(checkoutLink);
    }

    // Dùng khi webhook từ PayOS gọi về
    @PostMapping("/success/{orderCode}/{partnerId}/{packageId}")
    public ResponseEntity<?> markSuccess(@PathVariable Long orderCode, @PathVariable Long partnerId, @PathVariable Long packageId) {
        transactionService.markSuccess(orderCode, partnerId, packageId);
        return ResponseEntity.ok("Success updated");
    }

    @PostMapping("/fail/{orderCode}")
    public ResponseEntity<?> markFail(@PathVariable Long orderCode) {
        transactionService.markFailed(orderCode);
        return ResponseEntity.ok("Fail updated");
    }

    @GetMapping("/admin/dashboard/summary")
    public ResponseEntity<Map<String, Object>> getTransactionSummary() {
        return ResponseEntity.ok(transactionService.getTransactionSummary());
    }

    @GetMapping("/admin/dashboard/revenue-growth")
    public ResponseEntity<Map<String, Object>> getTransactionRevenueAndGrowth() {
        return ResponseEntity.ok(transactionService.getTransactionRevenueAndGrowth());
    }

    @GetMapping("/admin/dashboard/top-partners")
    public ResponseEntity<List<Map<String, Object>>> getTopPartners() {
        return ResponseEntity.ok(transactionService.getTopPartners());
    }
}
