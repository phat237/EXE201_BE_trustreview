package com.trustreview.trustreview.Repository;

import com.trustreview.trustreview.Entity.PremiumPackage;
import com.trustreview.trustreview.Entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PremiumPackageRepository extends JpaRepository<PremiumPackage, Long> {
}
