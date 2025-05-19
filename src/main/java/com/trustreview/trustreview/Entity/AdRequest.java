package com.trustreview.trustreview.Entity;

import com.trustreview.trustreview.Enums.AccountStatus;
import com.trustreview.trustreview.Enums.AdRequestStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter

public class AdRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String partnerName;

    @Column(unique = true, nullable = false)
    private String contactEmail;

    @Column(unique = true, nullable = false)
    private String phoneNumber;

    private String message;

    @Column(nullable = false, updatable = false)
    private LocalDateTime submittedAt;

    @Enumerated(EnumType.STRING)
    private AdRequestStatus status;

    @ManyToOne
    @JoinColumn(name="account_id", nullable = false)
    @JsonIgnore
    Account accountAdRequest;
}
