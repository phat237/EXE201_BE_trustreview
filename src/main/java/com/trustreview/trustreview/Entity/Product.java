package com.trustreview.trustreview.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.trustreview.trustreview.Enums.ProductCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter

public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String brandName;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String sourceUrl;

    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    @OneToMany(mappedBy = "productReview", cascade = CascadeType.ALL)
    @JsonIgnore
    Set<Review> reviews;

    @OneToMany(mappedBy = "productAggregatedSource", cascade = CascadeType.ALL)
    @JsonIgnore
    Set<AggregatedSource> aggregatedSources;
}
