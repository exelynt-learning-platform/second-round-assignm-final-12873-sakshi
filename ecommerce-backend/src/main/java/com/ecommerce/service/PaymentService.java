package com.ecommerce.service;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.User;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final OrderRepository orderRepo;
    private final UserRepository userRepo;
    private final CartRepository cartRepo;

    public PaymentService(OrderRepository orderRepo,
                          UserRepository userRepo,
                          CartRepository cartRepo) {
        this.orderRepo = orderRepo;
        this.userRepo = userRepo;
        this.cartRepo = cartRepo;
    }

    public String processPayment(Long orderId) {

        // ✅ Order fetch
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // ✅ Already paid
        if ("SUCCESS".equals(order.getPaymentStatus())) {
            throw new RuntimeException("Payment already completed");
        }

        // ✅ Validate items
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new RuntimeException("Order has no items");
        }

        // ✅ Validate user
        User user = order.getUser();
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        double total = order.getTotalPrice();

        // ✅ Validate total
        if (total <= 0) {
            throw new RuntimeException("Invalid order amount");
        }

        // ❌ Insufficient balance
        if (user.getAmount() < total) {
            order.setPaymentStatus("FAILED");
            orderRepo.save(order);
            return "FAILED";
        }

        // 💰 Deduct balance
        user.setAmount(user.getAmount() - total);
        userRepo.save(user);

        // ✅ Mark success
        order.setPaymentStatus("SUCCESS");
        orderRepo.save(order);

        // 🛒 Clear cart
        cartRepo.deleteByUser(user);

        return "SUCCESS";
    }

    public void markFailed(Long orderId) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if ("SUCCESS".equals(order.getPaymentStatus())) {
            throw new RuntimeException("Cannot mark successful order as failed");
        }

        order.setPaymentStatus("FAILED");
        orderRepo.save(order);
    }
}