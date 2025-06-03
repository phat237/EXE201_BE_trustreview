package com.trustreview.trustreview.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter

public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    private String description;

    private Integer requiredPoint;

    private boolean isActive;

    @Column(nullable = false)
    private String batchCode;

    @Column(nullable = false)
    private boolean hasBeenRedeemed = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name="partner_id", nullable = false)
    @JsonIgnore
    Partner partnerVoucher;

    @OneToMany(mappedBy = "voucherUser", cascade = CascadeType.ALL)
    @JsonIgnore
    Set<UserVoucher> userVouchers;
}
