package com.ecommerce.service;

import com.ecommerce.dto.ProductRequest;
import com.ecommerce.dto.ProductResponse;
import com.ecommerce.entity.Product;
import com.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    // 🔥 CREATE
    public ProductResponse create(ProductRequest req) {

        Product p = new Product();
        p.setName(req.getName());
        p.setDescription(req.getDescription());
        p.setPrice(req.getPrice());
        p.setStockQuantity(req.getStockQuantity());
        p.setImageUrl(req.getImageUrl());

        Product saved = repo.save(p);

        return new ProductResponse(
                saved.getId(),
                saved.getName(),
                saved.getDescription(),
                saved.getPrice(),
                saved.getStockQuantity(),   // ✅ FIX
                saved.getImageUrl()         // ✅ FIX
        );
    }

    // 🔥 GET ALL
    public List<ProductResponse> getAll() {
        return repo.findAll()
                .stream()
                .map(p -> new ProductResponse(
                        p.getId(),
                        p.getName(),
                        p.getDescription(),
                        p.getPrice(),
                        p.getStockQuantity(),   // ✅ FIX
                        p.getImageUrl()         // ✅ FIX
                ))
                .collect(Collectors.toList());
    }

    // 🔥 GET BY ID
    public ProductResponse getById(Long id) {
        Product p = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getStockQuantity(),   // ✅ FIX
                p.getImageUrl()         // ✅ FIX
        );
    }

    // 🔥 UPDATE
    public ProductResponse update(Long id, ProductRequest req) {

        Product existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        existing.setName(req.getName());
        existing.setDescription(req.getDescription());
        existing.setPrice(req.getPrice());
        existing.setStockQuantity(req.getStockQuantity());
        existing.setImageUrl(req.getImageUrl());

        Product updated = repo.save(existing);

        return new ProductResponse(
                updated.getId(),
                updated.getName(),
                updated.getDescription(),
                updated.getPrice(),
                updated.getStockQuantity(),   // ✅ FIX
                updated.getImageUrl()         // ✅ FIX
        );
    }

    // 🔥 DELETE
    public void delete(Long id) {
        repo.deleteById(id);
    }
}