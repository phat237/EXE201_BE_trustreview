package com.trustreview.trustreview.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter

public class PartnerPackage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    private boolean isActive;

    @ManyToOne
    @JoinColumn(name="package_id", nullable = false)
    @JsonIgnore
    PremiumPackage premiumPartner;

    @ManyToOne
    @JoinColumn(name="partner_id", nullable = false)
    @JsonIgnore
    Partner partnerPackage;
}
