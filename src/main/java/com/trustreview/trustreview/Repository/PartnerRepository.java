package com.trustreview.trustreview.Repository;

import com.trustreview.trustreview.Entity.Partner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartnerRepository extends JpaRepository<Partner, Long> {
//    Optional<Partner> findById(Long id);
}