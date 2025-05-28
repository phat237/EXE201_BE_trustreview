package com.trustreview.trustreview.Service;

import com.trustreview.trustreview.Entity.PremiumPackage;
import com.trustreview.trustreview.Repository.PremiumPackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PremiumPackageService {

    @Autowired
    private PremiumPackageRepository premiumPackageRepository;

    public List<PremiumPackage> getAllPackages() {
        return premiumPackageRepository.findAll();
    }

    public PremiumPackage getPackageById(Long id) {
        return premiumPackageRepository.findById(id)
                .orElseThrow(() -> new BadCredentialsException("Gói không tồn tại!"));
    }

    public PremiumPackage createPackage(PremiumPackage premiumPackage) {
        return premiumPackageRepository.save(premiumPackage);
    }

    public PremiumPackage updatePackage(Long id, PremiumPackage updatedPackage) {
        PremiumPackage existing = premiumPackageRepository.findById(id)
                .orElseThrow(() -> new BadCredentialsException("Không tìm thấy gói để cập nhật!"));

        existing.setName(updatedPackage.getName());
        existing.setDescription(updatedPackage.getDescription());
        existing.setPrice(updatedPackage.getPrice());
        existing.setDuration(updatedPackage.getDuration());

        return premiumPackageRepository.save(existing);
    }

    public void deletePackage(Long id) {
        if (!premiumPackageRepository.existsById(id)) {
            throw new BadCredentialsException("Không tìm thấy gói để xóa!");
        }
        premiumPackageRepository.deleteById(id);
    }
}
