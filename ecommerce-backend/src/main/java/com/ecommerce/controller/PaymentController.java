package com.ecommerce.controller;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderItem;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@Transactional
public class PaymentController {

    private final OrderRepository orderRepo;
    private final UserRepository userRepo;
    private final CartRepository cartRepo;

    @Value("${app.base-url}")
    private String baseUrl;

    public PaymentController(OrderRepository orderRepo,
                             UserRepository userRepo,
                             CartRepository cartRepo) {
        this.orderRepo = orderRepo;
        this.userRepo = userRepo;
        this.cartRepo = cartRepo;
    }

    // ✅ STEP 1: CREATE PAYMENT
    @PostMapping("/pay/{orderId}")
    public Map<String, String> pay(@PathVariable Long orderId) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // ✅ SAFE NULL CHECK
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new RuntimeException("Order has no items");
        }

        if (!"PENDING".equals(order.getPaymentStatus())) {
            throw new RuntimeException("Payment already processed");
        }

        Map<String, String> response = new HashMap<>();

        response.put("paymentUrl",
                baseUrl + "/api/payment/success/" + orderId);

        response.put("message", "Redirect user to payment gateway");

        return response;
    }

    // ✅ STEP 2: PAYMENT SUCCESS
    @PostMapping("/success/{orderId}")
    public Map<String, String> paymentSuccess(@PathVariable Long orderId) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // ✅ DOUBLE PAYMENT PREVENTION
        if ("SUCCESS".equals(order.getPaymentStatus())) {
            throw new RuntimeException("Payment already completed");
        }

        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new RuntimeException("Order has no items");
        }

        User user = userRepo.findById(order.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        double total = order.getTotalPrice();

        Map<String, String> response = new HashMap<>();

        // 💰 BALANCE CHECK
        if (user.getAmount() < total) {
            order.setPaymentStatus("FAILED");
            orderRepo.save(order);

            response.put("status", "FAILED");
            response.put("message", "Insufficient balance");
            return response;
        }

        // 📦 STOCK VALIDATION + REDUCTION
        for (OrderItem item : order.getItems()) {

            Product product = item.getProduct();

            if (product == null) {
                throw new RuntimeException("Invalid product in order");
            }

            int quantity = item.getQuantity();

            if (quantity <= 0) {
                throw new RuntimeException("Invalid quantity for " + product.getName());
            }

            if (product.getStockQuantity() < quantity) {
                throw new RuntimeException(
                        "Insufficient stock for " + product.getName());
            }

            // ✅ FINAL FIX (correct deduction)
            product.setStockQuantity(
                    product.getStockQuantity() - quantity
            );
        }

        // 💰 Deduct balance
        user.setAmount(user.getAmount() - total);
        userRepo.save(user);

        // 📦 Update order
        order.setPaymentStatus("SUCCESS");
        orderRepo.save(order);

        // 🛒 Clear cart
        cartRepo.deleteByUser(user);

        response.put("status", "SUCCESS");
        response.put("message", "Payment successful");

        return response;
    }

    // ✅ STEP 3: PAYMENT FAILED
    @PostMapping("/fail/{orderId}")
    public Map<String, String> paymentFail(@PathVariable Long orderId) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if ("SUCCESS".equals(order.getPaymentStatus())) {
            throw new RuntimeException("Cannot mark successful order as failed");
        }

        order.setPaymentStatus("FAILED");
        orderRepo.save(order);

        Map<String, String> response = new HashMap<>();
        response.put("status", "FAILED");
        response.put("message", "Payment failed");

        return response;
    }
}