package com.trustreview.trustreview.Entity;

import com.trustreview.trustreview.Enums.AccountRoles;
import com.trustreview.trustreview.Enums.AccountStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter

public class Account implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String displayName;

    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Enumerated(EnumType.STRING)
    private AccountRoles role;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "accountAdRequest", cascade = CascadeType.ALL)
    @JsonIgnore
    Set<AdRequest> adRequest;

    @OneToMany(mappedBy = "accountReview", cascade = CascadeType.ALL)
    @JsonIgnore
    Set<Review> reviews;

    @OneToMany(mappedBy = "accountReport", cascade = CascadeType.ALL)
    @JsonIgnore
    Set<ReviewReport> reviewReports;

    @OneToMany(mappedBy = "accountFeedback", cascade = CascadeType.ALL)
    @JsonIgnore
    Set<ReviewFeedback> reviewFeedbacks;



    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.toString()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}

