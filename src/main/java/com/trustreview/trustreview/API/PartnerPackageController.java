package com.trustreview.trustreview.API;

import com.trustreview.trustreview.Entity.*;
import com.trustreview.trustreview.Service.PartnerPackageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/partner-packages")
public class PartnerPackageController {

    @Autowired
    private PartnerPackageService partnerPackageService;

    @PostMapping("/{packageId}")
    public ResponseEntity<PartnerPackage> purchasePremiumPackage(@PathVariable Long packageId, HttpServletRequest request) {
        PartnerPackage partnerPackage = partnerPackageService.purchasePackage(packageId, request);
        return ResponseEntity.ok(partnerPackage);
    }

    @GetMapping
    public ResponseEntity<List<PartnerPackage>> getMyPurchasedPackages() {
        List<PartnerPackage> packages = partnerPackageService.getMyPackages();
        return ResponseEntity.ok(packages);
    }

    @GetMapping("/available")
    public ResponseEntity<List<PremiumPackage>> getAvailablePremiumPackages() {
        List<PremiumPackage> packages = partnerPackageService.getAllAvailablePremiumPackages();
        return ResponseEntity.ok(packages);
    }

    @PutMapping("/{partnerPackageId}/deactivate")
    public ResponseEntity<String> deactivatePackage(@PathVariable Long partnerPackageId) {
        partnerPackageService.deactivatePackage(partnerPackageId);
        return ResponseEntity.ok("Gói đã được vô hiệu hóa.");
    }
}
