package com.trustreview.trustreview.Repository;

import com.trustreview.trustreview.Entity.Partner;
import com.trustreview.trustreview.Entity.PartnerPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartnerPackageRepository extends JpaRepository<PartnerPackage, Long> {
    List<PartnerPackage> findByPartnerPackage(Partner partner);
}
