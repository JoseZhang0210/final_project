package com.hotel.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hotel.entity.Product;
import com.hotel.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(
            ProductRepository productRepository) {

        this.productRepository = productRepository;
    }

    // 查全部商品
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    // 查單一商品
    public Product findById(Integer id) {
        return productRepository
                .findById(id)
                .orElse(null);
    }

    // 儲存商品
    public Product save(Product product) {
        return productRepository.save(product);
    }

    // 刪除商品
    public void deleteById(Integer id) {
        productRepository.deleteById(id);
    }

    // 查詢販售中的商品
    public List<Product> getActiveProducts() {
        return productRepository
                .findByStatusOrderByProductIdAsc("ACTIVE");
    }
}