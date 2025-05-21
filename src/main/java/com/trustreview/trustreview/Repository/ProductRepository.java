package com.trustreview.trustreview.Repository;

import com.trustreview.trustreview.Entity.Account;
import com.trustreview.trustreview.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findProductById(Long id);
}
