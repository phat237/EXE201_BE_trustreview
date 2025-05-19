package com.trustreview.trustreview.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter

public class AggregatedSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sourceName;

    private float ratingAvg;

    private Integer reviewCount;

    private String AISumary;

    @Column(nullable = false, updatable = false)
    private LocalDateTime lastUpdate;

    @ManyToOne
    @JoinColumn(name="product_id", nullable = false)
    @JsonIgnore
    Product productAggregatedSource;
}
