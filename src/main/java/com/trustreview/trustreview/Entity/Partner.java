package com.trustreview.trustreview.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter

public class Partner extends Account{

    @Column(nullable = false)
    private String companyName;

    @Column(unique = true)
    private String businessRegistrationNumber;

    private String website;

    @Column(nullable = false)
    private String contactPhone;

    private Double money;

    @OneToMany(mappedBy = "partnerPackage", cascade = CascadeType.ALL)
    @JsonIgnore
    Set<PartnerPackage> partnerPackages;

    @OneToMany(mappedBy = "partnerFeedback", cascade = CascadeType.ALL)
    @JsonIgnore
    Set<PartnerFeedback> partnerFeedbacks;

    @OneToMany(mappedBy = "partnerVoucher", cascade = CascadeType.ALL)
    @JsonIgnore
    Set<Voucher> partnerVouchers;
}
