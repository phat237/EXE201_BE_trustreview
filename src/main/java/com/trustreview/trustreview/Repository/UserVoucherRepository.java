package com.trustreview.trustreview.Repository;

import com.trustreview.trustreview.Entity.UserVoucher;
import com.trustreview.trustreview.Entity.Users;
import com.trustreview.trustreview.Entity.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserVoucherRepository extends JpaRepository<UserVoucher, Long> {

    boolean existsByUserVoucherAndVoucherUser(Users user, Voucher voucher);

    Page<UserVoucher> findByUserVoucher_Id(Long userId, Pageable pageable);

    boolean existsByUserVoucherAndVoucherUser_BatchCode(Users user, String batchCode);

}
