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

    // 🔥 COMMON VALIDATION
    private void validate(ProductRequest req) {
        if (req == null) {
            throw new IllegalArgumentException("Product request cannot be null");
        }
        if (req.getName() == null || req.getName().isBlank()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (req.getPrice() < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (req.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
    }

    // 🔥 DTO MAPPER (REMOVES DUPLICATION)
    private ProductResponse mapToResponse(Product p) {
        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getStockQuantity(),
                p.getImageUrl()
        );
    }

    // 🔥 CREATE
    public ProductResponse create(ProductRequest req) {

        validate(req);

        Product p = new Product();
        p.setName(req.getName());
        p.setDescription(req.getDescription());
        p.setPrice(req.getPrice());
        p.setStockQuantity(req.getStockQuantity());
        p.setImageUrl(req.getImageUrl());

        return mapToResponse(repo.save(p));
    }

    // 🔥 GET ALL
    public List<ProductResponse> getAll() {
        return repo.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 🔥 GET BY ID
    public ProductResponse getById(Long id) {
        Product p = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return mapToResponse(p);
    }

    // 🔥 UPDATE
    public ProductResponse update(Long id, ProductRequest req) {

        validate(req);

        Product existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        existing.setName(req.getName());
        existing.setDescription(req.getDescription());
        existing.setPrice(req.getPrice());
        existing.setStockQuantity(req.getStockQuantity());
        existing.setImageUrl(req.getImageUrl());

        return mapToResponse(repo.save(existing));
    }

    // 🔥 DELETE
    public void delete(Long id) {

        Product existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        repo.delete(existing);
    }
}