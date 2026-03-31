package com.ecommerce.controller;

import com.ecommerce.entity.Order;
import com.ecommerce.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    // 🔥 CREATE ORDER
    @PostMapping
    public Order createOrder(@RequestParam String address,
                             Authentication auth) {

        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Address cannot be empty");
        }

        String username = auth.getName();
        return service.createOrder(username, address);
    }

    // 🔥 GET USER ORDERS
    @GetMapping
    public List<Order> getOrders(Authentication auth) {

        String username = auth.getName();
        return service.getUserOrders(username);
    }

    // ❌ REMOVED PAYMENT API (handled in PaymentController)
}