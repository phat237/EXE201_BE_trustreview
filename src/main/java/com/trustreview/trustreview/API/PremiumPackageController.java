package com.trustreview.trustreview.API;

import com.trustreview.trustreview.Entity.PremiumPackage;
import com.trustreview.trustreview.Service.PremiumPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/premium-packages")
public class PremiumPackageController {

    @Autowired
    private PremiumPackageService premiumPackageService;

    @GetMapping
    public ResponseEntity<List<PremiumPackage>> getAllPackages() {
        return ResponseEntity.ok(premiumPackageService.getAllPackages());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PremiumPackage> getPackageById(@PathVariable Long id) {
        return ResponseEntity.ok(premiumPackageService.getPackageById(id));
    }

    @PostMapping
    public ResponseEntity<PremiumPackage> createPackage(@RequestBody PremiumPackage premiumPackage) {
        return ResponseEntity.ok(premiumPackageService.createPackage(premiumPackage));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PremiumPackage> updatePackage(@PathVariable Long id, @RequestBody PremiumPackage updatedPackage) {
        return ResponseEntity.ok(premiumPackageService.updatePackage(id, updatedPackage));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePackage(@PathVariable Long id) {
        premiumPackageService.deletePackage(id);
        return ResponseEntity.ok("Đã xóa gói thành công.");
    }
}
