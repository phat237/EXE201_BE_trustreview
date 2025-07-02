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


}
