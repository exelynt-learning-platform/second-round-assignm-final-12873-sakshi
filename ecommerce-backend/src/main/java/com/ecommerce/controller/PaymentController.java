package com.ecommerce.controller;

import com.ecommerce.service.PaymentService;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.entity.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderRepository orderRepo;

    @Value("${app.base-url}")
    private String baseUrl;

    public PaymentController(PaymentService paymentService,
                             OrderRepository orderRepo) {
        this.paymentService = paymentService;
        this.orderRepo = orderRepo;
    }

    // 🔥 STEP 1: CREATE PAYMENT LINK
    @PostMapping("/pay/{orderId}")
    public Map<String, String> pay(@PathVariable Long orderId) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new RuntimeException("Order has no items");
        }

        if (!"PENDING".equals(order.getPaymentStatus())) {
            throw new RuntimeException("Payment already processed");
        }

        Map<String, String> response = new HashMap<>();
        response.put("paymentUrl", baseUrl + "/api/payment/success/" + orderId);
        return response;
    }

    // 🔥 STEP 2: SUCCESS
    @PostMapping("/success/{orderId}")
    public Map<String, String> success(@PathVariable Long orderId) {

        String status = paymentService.processPayment(orderId);

        Map<String, String> response = new HashMap<>();
        response.put("status", status);
        response.put("message",
                "SUCCESS".equals(status) ? "Payment successful" : "Payment failed");

        return response;
    }

    // 🔥 STEP 3: FAIL
    @PostMapping("/fail/{orderId}")
    public Map<String, String> fail(@PathVariable Long orderId) {

        paymentService.markFailed(orderId);

        Map<String, String> response = new HashMap<>();
        response.put("status", "FAILED");
        response.put("message", "Payment failed");

        return response;
    }
}