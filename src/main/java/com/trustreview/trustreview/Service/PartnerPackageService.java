package com.trustreview.trustreview.Service;

import com.trustreview.trustreview.Entity.*;
import com.trustreview.trustreview.Repository.AuthenticationRepository;
import com.trustreview.trustreview.Repository.PartnerPackageRepository;
import com.trustreview.trustreview.Repository.PartnerRepository;
import com.trustreview.trustreview.Repository.PremiumPackageRepository;
import com.trustreview.trustreview.Utils.AccountUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PartnerPackageService {

    @Autowired
    private PremiumPackageRepository premiumPackageRepository;

    @Autowired
    private PartnerPackageRepository partnerPackageRepository;

    @Autowired
    private AccountUtils accountUtils;

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private PartnerRepository partnerRepository;

    public PartnerPackage purchasePackage(Long packageId, Long partnerId) {
        PremiumPackage premium = premiumPackageRepository.findById(packageId)
                .orElseThrow(() -> new BadCredentialsException("Gói premium không tồn tại"));

        Partner partner = partnerRepository.findById(partnerId).orElse(null);
        if (partner == null){
            throw new BadCredentialsException("Partner này không tồn tại");
        }

        double currentMoney = partner.getMoney();
        if (currentMoney < premium.getPrice()) {
            throw new BadCredentialsException("Số dư không đủ để mua gói premium này");
        }
        partner.setMoney(currentMoney - premium.getPrice());

        PartnerPackage partnerPackage = new PartnerPackage();
        partnerPackage.setPremiumPartner(premium);
        partnerPackage.setPartnerPackage(partner);
        partnerPackage.setStartDate(LocalDateTime.now());
        partnerPackage.setEndDate(LocalDateTime.now().plusDays(premium.getDuration()));
        partnerPackage.setActive(true);

        authenticationRepository.save(partner);
        return partnerPackageRepository.save(partnerPackage);
    }

    public List<PartnerPackage> getMyPackages() {
        Account account = accountUtils.getAccountCurrent();
        if (!(account instanceof Partner partner)) {
            throw new BadCredentialsException("Chỉ partner mới được phép xem gói đã mua");
        }
        return partnerPackageRepository.findByPartnerPackage(partner);
    }

    public List<PremiumPackage> getAllAvailablePremiumPackages() {
        return premiumPackageRepository.findAll();
    }

    public void deactivatePackage(Long partnerPackageId) {
        PartnerPackage pp = partnerPackageRepository.findById(partnerPackageId)
                .orElseThrow(() -> new BadCredentialsException("Gói không tồn tại"));
        pp.setActive(false);
        partnerPackageRepository.save(pp);
    }
}
