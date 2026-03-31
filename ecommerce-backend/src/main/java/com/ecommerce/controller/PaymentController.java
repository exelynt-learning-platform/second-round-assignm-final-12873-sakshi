package com.ecommerce.controller;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.repository.CartRepository; // 🔥 ADD THIS
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
    private final CartRepository cartRepo; // 🔥 ADD

    public PaymentController(OrderRepository orderRepo,
                             UserRepository userRepo,
                             CartRepository cartRepo) { // 🔥 ADD
        this.orderRepo = orderRepo;
        this.userRepo = userRepo;
        this.cartRepo = cartRepo; // 🔥 ADD
    }

    // 🔥 STEP 1: CREATE PAYMENT
    @PostMapping("/pay/{orderId}")
    public Map<String, String> pay(@PathVariable Long orderId) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getPaymentStatus().equals("PENDING")) {
            throw new RuntimeException("Payment already processed");
        }

        Map<String, String> response = new HashMap<>();

        response.put("paymentUrl",
                "http://localhost:8080/api/payment/success/" + orderId);

        response.put("message", "Redirect user to payment gateway");

        return response;
    }

    // 🔥 STEP 2: PAYMENT SUCCESS
    @PostMapping("/success/{orderId}")
    public String paymentSuccess(@PathVariable Long orderId) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 🔥 Prevent double payment
        if (order.getPaymentStatus().equals("SUCCESS")) {
            throw new RuntimeException("Payment already completed");
        }

        // 🔥 IMPORTANT FIX: fresh user from DB
        User user = userRepo.findById(order.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        double total = order.getTotalPrice();

        // ❌ FAIL CASE
        if (user.getAmount() < total) {
            order.setPaymentStatus("FAILED");
            orderRepo.save(order);
            return "Payment Failed - Insufficient Balance";
        }

        // 🔥 STOCK REDUCE
        for (Product product : order.getProducts()) {

            if (product.getStockQuantity() <= 0) {
                throw new RuntimeException(product.getName() + " is out of stock");
            }

            product.setStockQuantity(product.getStockQuantity() - 1);
        }

        // 💰 Deduct money
        user.setAmount(user.getAmount() - total);
        userRepo.save(user); // ✅ MUST

        // 📦 Order success
        order.setPaymentStatus("SUCCESS");
        orderRepo.save(order);

        // 🛒 CLEAR CART (FINAL FIX)
        cartRepo.deleteByUser(user);

        return "Payment Successful";
    }

    // 🔥 STEP 3: PAYMENT FAILED
    @PostMapping("/fail/{orderId}")
    public String paymentFail(@PathVariable Long orderId) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getPaymentStatus().equals("SUCCESS")) {
            throw new RuntimeException("Cannot mark successful order as failed");
        }

        order.setPaymentStatus("FAILED");
        orderRepo.save(order);

        return "Payment Failed";
    }
}