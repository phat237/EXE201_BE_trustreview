package com.trustreview.trustreview.Service;

import com.trustreview.trustreview.Entity.Product;
import com.trustreview.trustreview.Entity.Review;
import com.trustreview.trustreview.Enums.ProductCategory;
import com.trustreview.trustreview.Model.ProductRequest;
import com.trustreview.trustreview.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service

public class ProductService {

    @Autowired
    ProductRepository productRepository;

    public Product addProduct(ProductCategory category, ProductRequest productRequest) {
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setBrandName(productRequest.getBrandName());
        product.setCreatedAt(LocalDateTime.now());
        product.setSourceUrl(productRequest.getSourceUrl());
        product.setCategory(category);
        return productRepository.save(product);
    }

    public Product getAProduct(Long productId) {
        Product product = productRepository.findProductById(productId);
        if (product == null){
            throw new BadCredentialsException("Đã xảy ra lỗi, vui lòng thử lại!");
        }
        return product;
    }

    public Product updateAProduct(Long productId, ProductCategory category, ProductRequest productRequest) {
        Product product = productRepository.findProductById(productId);
        if (product == null){
            throw new BadCredentialsException("Đã xảy ra lỗi, vui lòng thử lại!");
        }
            if (productRequest.getName() != null){
                product.setName(productRequest.getName());
            }
            if (productRequest.getBrandName() != null){
                product.setBrandName(productRequest.getBrandName());
            }
            if (productRequest.getSourceUrl() != null){
                product.setSourceUrl(productRequest.getSourceUrl());
            }
            if (!product.getCategory().equals(category)){
                product.setCategory(category);
            }
        return productRepository.save(product);
    }

    public String deleteAProduct(Long productId) {
        if (productRepository.findProductById(productId) != null){
            productRepository.deleteById(productId);
            return "Đã xóa sản phẩm thành công!";
        } else {
            return "Đã có lỗi xảy ra, xóa sản phẩm không thành công!";
        }
    }

    public Page<Product> getPagingProduct(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return products;
    }

    public Page<Product> getProductsSortedByRating(Pageable pageable) {
        return productRepository.findAllOrderByRating(pageable);
    }

    public Page<Product> getRelatedProducts(Long productId, Pageable pageable) {
        Product base = productRepository.findProductById(productId);
        if (base == null) throw new RuntimeException("Không tìm thấy sản phẩm");

        String[] tokens = base.getName().toLowerCase().split(" ");
        String kw1 = tokens.length > 0 ? tokens[0] : "";
        String kw2 = tokens.length > 1 ? tokens[1] : "";

        List<Product> result = productRepository.findRelatedByName(productId, base.getCategory(), kw1, kw2);

        if (result.size() < pageable.getPageSize()) {
            List<Product> more = productRepository.findByCategoryExcept(productId, base.getCategory());
            Set<Long> existIds = result.stream().map(Product::getId).collect(Collectors.toSet());
            for (Product p : more) {
                if (!existIds.contains(p.getId())) {
                    result.add(p);
                    if (result.size() == pageable.getPageSize()) break;
                }
            }
        }

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), result.size());
        List<Product> pageContent = result.subList(start, end);
        return new PageImpl<>(pageContent, pageable, result.size());
    }


}
