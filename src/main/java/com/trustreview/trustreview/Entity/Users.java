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
@Table(name = "app_user")
public class Users extends Account{

    @Column(nullable = false)
    private String displayName;

    private LocalDateTime bannedUntil;

    private Integer point;

    @OneToMany(mappedBy = "userPoint", cascade = CascadeType.ALL)
    @JsonIgnore
    Set<UserPoint> userPoints;

    @OneToMany(mappedBy = "userVoucher", cascade = CascadeType.ALL)
    @JsonIgnore
    Set<UserVoucher> userVouchers;

    @OneToMany(mappedBy = "userFeedback", cascade = CascadeType.ALL)
    @JsonIgnore
    Set<ReviewFeedback> reviewFeedbacks;

    @OneToMany(mappedBy = "userReport", cascade = CascadeType.ALL)
    @JsonIgnore
    Set<ReviewReport> reviewReports;

    @OneToMany(mappedBy = "userReview", cascade = CascadeType.ALL)
    @JsonIgnore
    Set<Review> reviews;

}
