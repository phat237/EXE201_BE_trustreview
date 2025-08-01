package com.trustreview.trustreview.Repository;

import com.trustreview.trustreview.Entity.Account;
import com.trustreview.trustreview.Entity.Product;
import com.trustreview.trustreview.Enums.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository

public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findProductById(Long id);

    Page<Product> findAll(Pageable pageable);

    @Query("""
    SELECT p 
    FROM Product p 
    JOIN p.reviews r 
    GROUP BY p 
    ORDER BY AVG(r.rating) DESC, COUNT(r) DESC
    """)
    Page<Product> findAllOrderByRating(Pageable pageable);

    // Query A: Sản phẩm liên quan theo từ khóa tên
    @Query("""
    SELECT p FROM Product p
    JOIN p.reviews r
    WHERE p.id <> :productId
      AND p.category = :category
      AND (
          LOWER(p.name) LIKE %:kw1%
          OR LOWER(p.name) LIKE %:kw2%
      )
    GROUP BY p
    ORDER BY AVG(r.rating) DESC, COUNT(r) DESC
""")
    List<Product> findRelatedByName(@Param("productId") Long productId,
                                    @Param("category") ProductCategory category,
                                    @Param("kw1") String kw1,
                                    @Param("kw2") String kw2);


    // Query B: Sản phẩm cùng category (loại trừ productId)
    @Query("""
    SELECT p FROM Product p
    WHERE p.id <> :productId
      AND p.category = :category
""")
    List<Product> findByCategoryExcept(@Param("productId") Long productId,
                                       @Param("category") ProductCategory category);

    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.brandName = :brandName")
    long countByBrandName(@Param("brandName") String brandName);

    @Query("SELECT SUM(p.viewCount) FROM Product p WHERE p.brandName = :brandName")
    Long getTotalViewCountByBrand(@Param("brandName") String brandName);

    @Query("""
    SELECT p
    FROM Product p
    JOIN p.reviews r
    WHERE p.brandName = :brandName
    GROUP BY p
    HAVING AVG(r.rating) >= :minRating AND AVG(r.rating) < (:minRating + 1)
    ORDER BY AVG(r.rating) DESC, COUNT(r) DESC
""")
    Page<Product> findByBrandNameAndRatingRange(
            @Param("brandName") String brandName,
            @Param("minRating") int minRating,
            Pageable pageable);

    Page<Product> findByCategory(ProductCategory category, Pageable pageable);

    //
    long countByCategory(ProductCategory category);

    @Query("SELECT SUM(p.viewCount) FROM Product p")
    Long sumViewCount();

    @Query("SELECT p FROM Product p ORDER BY p.viewCount DESC")
    Page<Product> findTopByViewCount(Pageable pageable);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.createdAt BETWEEN :start AND :end")
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT p.category, AVG(r.rating) FROM Product p LEFT JOIN p.reviews r GROUP BY p.category")
    List<Object[]> findAverageRatingByCategory();
}
