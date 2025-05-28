package com.trustreview.trustreview.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.trustreview.trustreview.Enums.PartnerReportStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter

public class PartnerReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PartnerReportStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createAt;

    private LocalDateTime approveAt;

    @ManyToOne
    @JoinColumn(name="product_id", nullable = false)
    @JsonIgnore
    Product productPartnerReport;
}
