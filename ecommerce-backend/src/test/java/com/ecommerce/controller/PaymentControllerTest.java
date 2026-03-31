package com.ecommerce.controller;

import com.ecommerce.entity.*;
import com.ecommerce.repository.*;
import com.ecommerce.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentControllerTest {

    @Mock
    private OrderRepository orderRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private CartRepository cartRepo;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    public PaymentControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ TEST 1: SUCCESS
    @Test
    void testPaymentSuccess() {

        User user = new User();
        user.setId(1L);
        user.setAmount(10000);

        Product product = new Product();
        product.setName("Laptop");
        product.setStockQuantity(5);
        product.setPrice(5000);

        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(1);

        Order order = new Order();
        order.setUser(user);
        order.setTotalPrice(5000);
        order.setPaymentStatus("PENDING");
        order.setItems(List.of(item));

        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));

        // 🔥 FIX
        when(paymentService.processPayment(1L)).thenReturn("SUCCESS");

        Map<String, String> result = paymentController.success(1L);

        assertEquals("SUCCESS", result.get("status"));
        assertEquals("Payment successful", result.get("message"));
    }

    // ❌ TEST 2: INSUFFICIENT BALANCE
    @Test
    void testPaymentFail_InsufficientBalance() {

        User user = new User();
        user.setId(1L);
        user.setAmount(1000);

        Product product = new Product();
        product.setName("Laptop");
        product.setStockQuantity(5);
        product.setPrice(5000);

        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(1);

        Order order = new Order();
        order.setUser(user);
        order.setTotalPrice(5000);
        order.setPaymentStatus("PENDING");
        order.setItems(List.of(item));

        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));

        // 🔥 FIX
        when(paymentService.processPayment(1L)).thenReturn("FAILED");

        Map<String, String> result = paymentController.success(1L);

        assertEquals("FAILED", result.get("status"));
        assertEquals("Payment failed", result.get("message"));
    }

    // ❌ TEST 3: ALREADY PAID
    @Test
    void testPaymentAlreadyCompleted() {

        when(paymentService.processPayment(1L))
                .thenThrow(new RuntimeException("Payment already completed"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> paymentController.success(1L));

        assertEquals("Payment already completed", ex.getMessage());
    }
}