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

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if ("SUCCESS".equals(order.getPaymentStatus())) {
            throw new RuntimeException("Payment already completed");
        }

        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new RuntimeException("Order has no items");
        }

        User user = order.getUser();

        double total = order.getTotalPrice();

        if (user.getAmount() < total) {
            order.setPaymentStatus("FAILED");
            orderRepo.save(order);
            return "FAILED";
        }

        // 💰 Deduct balance
        user.setAmount(user.getAmount() - total);
        userRepo.save(user);

        // ✅ DO NOT REDUCE STOCK AGAIN (already done in OrderService)

        order.setPaymentStatus("SUCCESS");
        orderRepo.save(order);

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