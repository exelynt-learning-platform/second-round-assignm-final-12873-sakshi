package com.ecommerce.controller;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.User;
import com.ecommerce.entity.Product;   // 🔥 ADD THIS
import java.util.List;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.repository.CartRepository;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentControllerTest {

    @Mock
    private OrderRepository orderRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private CartRepository cartRepo; // 🔥 REQUIRED (controller me hai)

    @InjectMocks
    private PaymentController paymentController;

    public PaymentControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ TEST 1: SUCCESS CASE
    @Test
    void testPaymentSuccess() {

        // 👉 Dummy user
        User user = new User();
        user.setId(1L);
        user.setAmount(10000);

        // 👉 Dummy product
        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setStockQuantity(5);
        product.setPrice(5000);

        // 👉 Dummy order
        Order order = new Order();
        order.setUser(user);
        order.setTotalPrice(5000);
        order.setPaymentStatus("PENDING");

        // 🔥 IMPORTANT FIX
        order.setProducts(List.of(product));

        // 👉 Mock DB
        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        // 👉 Call method
        String result = paymentController.paymentSuccess(1L);

        // 👉 Verify
        assertEquals("Payment Successful", result);
        assertEquals(5000, user.getAmount());
        assertEquals("SUCCESS", order.getPaymentStatus());

        verify(cartRepo).deleteByUser(user);
    }

    // ❌ TEST 2: INSUFFICIENT BALANCE
    @Test
    void testPaymentFail_InsufficientBalance() {

        // 👉 Dummy user
        User user = new User();
        user.setId(1L);
        user.setAmount(1000);

        // 👉 Dummy order
        Order order = new Order();
        order.setUser(user);
        order.setTotalPrice(5000);
        order.setPaymentStatus("PENDING");

        // 👉 Mock DB
        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        // 👉 Call method
        String result = paymentController.paymentSuccess(1L);

        // 👉 Verify
        assertEquals("Payment Failed - Insufficient Balance", result);
        assertEquals("FAILED", order.getPaymentStatus());
    }

    // ❌ TEST 3: ALREADY PAID
    @Test
    void testPaymentAlreadyCompleted() {

        User user = new User();
        user.setId(1L);
        user.setAmount(10000);

        Order order = new Order();
        order.setUser(user);
        order.setTotalPrice(5000);
        order.setPaymentStatus("SUCCESS"); // 🔥 already paid

        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> paymentController.paymentSuccess(1L));

        assertEquals("Payment already completed", ex.getMessage());
    }
}