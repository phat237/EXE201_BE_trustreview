package com.trustreview.trustreview.API;

import com.trustreview.trustreview.Entity.Account;
import com.trustreview.trustreview.Entity.Product;
import com.trustreview.trustreview.Entity.Review;
import com.trustreview.trustreview.Enums.ProductCategory;
import com.trustreview.trustreview.Model.ProductRequest;
import com.trustreview.trustreview.Service.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/products")
@SecurityRequirement(name = "bearerAuth")

public class ProductController {

    @Autowired
    ProductService productService;

    @PostMapping("/{category}")
    public ResponseEntity<Product> createdProduct(@PathVariable ProductCategory category, @RequestBody ProductRequest productRequest) {
        Product product = productService.addProduct(category, productRequest);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProduct(@PathVariable Long productId) {
        Product product = productService.getAProduct(productId);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{productId}/{category}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long productId, ProductCategory category, @RequestBody ProductRequest productRequest) {
        Product product = productService.updateAProduct(productId, category, productRequest);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.deleteAProduct(productId));
    }

    @GetMapping("/{page}/{size}/paging")
    public ResponseEntity<Page<Product>> getProductPaging(@PathVariable int page, int size) {
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(productService.getPagingProduct(pageable));
    }

    @GetMapping("/sorted-by-rating")
    public ResponseEntity<Page<Product>> getProductsSortedByRating(
            @RequestParam int page,
            @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.getProductsSortedByRating(pageable));
    }

    @GetMapping("/{productId}/related")
    public ResponseEntity<Page<Product>> getRelatedProducts(
            @PathVariable Long productId,
            @RequestParam int page,
            @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.getRelatedProducts(productId, pageable));
    }

    @GetMapping("/search/paging")
    public ResponseEntity<Page<Product>> searchProductsPaged(
            @RequestParam("keyword") String keyword,
            @RequestParam int page,
            @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> result = productService.searchProductsPaged(keyword, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/count-by-partner")
    public ResponseEntity<Long> countByPartner() {
        long count = productService.countProductsByPartner();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/total-views-by-brand")
    public ResponseEntity<Long> getTotalViewsByBrand() {
        long totalViews = productService.getTotalViewCountByBrand();
        return ResponseEntity.ok(totalViews);
    }

    @GetMapping("/by-brand-rating")
    public ResponseEntity<Page<Product>> getProductsByBrandAndRating(
            @RequestParam int rating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> result = productService.getProductsByBrandAndRating(rating, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/search-by-category")
    public ResponseEntity<Page<Product>> getProductsByCategory(
            @RequestParam ProductCategory category,
            @RequestParam int page,
            @RequestParam int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> result = productService.getProductsByCategory(category, pageable);
        return ResponseEntity.ok(result);
    }
}
