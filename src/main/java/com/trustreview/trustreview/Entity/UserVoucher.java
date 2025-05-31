package com.trustreview.trustreview.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter

public class UserVoucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private LocalDateTime redeemedAt;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = true)
    @JsonIgnore
    Users userVoucher;

    @ManyToOne
    @JoinColumn(name="voucher_id", nullable = true)
    @JsonIgnore
    Voucher voucherUser;
}
