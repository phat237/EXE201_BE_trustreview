package com.trustreview.trustreview.Repository;

import com.trustreview.trustreview.Entity.Voucher;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    boolean existsByCode(String code);

    @Query("SELECT MAX(CAST(SUBSTRING(v.batchCode, LENGTH(v.batchCode) - 2, 3) AS int)) " +
            "FROM Voucher v " +
            "WHERE v.partnerVoucher.id = :partnerId " +
            "AND v.batchCode LIKE CONCAT(:batchPrefix, '%')")
    Integer findMaxBatchNumberToday(@Param("partnerId") Long partnerId,
                                    @Param("batchPrefix") String batchPrefix);

    @Modifying
    @Transactional
    @Query("""
    UPDATE Voucher v
    SET v.description = :description,
        v.requiredPoint = :requiredPoint,
        v.isActive = :isActive
    WHERE v.batchCode = :batchCode""")
    int updateVoucherBatchDetails(@Param("batchCode") String batchCode,
                                  @Param("description") String description,
                                  @Param("requiredPoint") Integer requiredPoint,
                                  @Param("isActive") boolean isActive);


}
