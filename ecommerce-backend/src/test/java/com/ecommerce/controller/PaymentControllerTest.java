package com.ecommerce.controller;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.User;
import com.ecommerce.entity.Product;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.repository.CartRepository;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentControllerTest {

    @Mock
    private OrderRepository orderRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private CartRepository cartRepo;

    @InjectMocks
    private PaymentController paymentController;

    public PaymentControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ TEST 1: SUCCESS CASE
    @Test
    void testPaymentSuccess() {

        User user = new User();
        user.setId(1L);
        user.setAmount(10000);

        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setStockQuantity(5);
        product.setPrice(5000);

        Order order = new Order();
        order.setUser(user);
        order.setTotalPrice(5000);
        order.setPaymentStatus("PENDING");
        order.setProducts(List.of(product));

        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        // 🔥 FIX: Map instead of String
        Map<String, String> result = paymentController.paymentSuccess(1L);

        assertEquals("SUCCESS", result.get("status"));
        assertEquals("Payment successful", result.get("message"));

        assertEquals(5000, user.getAmount());
        assertEquals("SUCCESS", order.getPaymentStatus());

        verify(cartRepo).deleteByUser(user);
    }

    // ❌ TEST 2: INSUFFICIENT BALANCE
    @Test
    void testPaymentFail_InsufficientBalance() {

        User user = new User();
        user.setId(1L);
        user.setAmount(1000);

        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setStockQuantity(5);
        product.setPrice(5000);

        Order order = new Order();
        order.setUser(user);
        order.setTotalPrice(5000);
        order.setPaymentStatus("PENDING");
        order.setProducts(List.of(product));

        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        Map<String, String> result = paymentController.paymentSuccess(1L);

        assertEquals("FAILED", result.get("status"));
        assertEquals("Insufficient balance", result.get("message"));

        assertEquals("FAILED", order.getPaymentStatus());
    }

    // ❌ TEST 3: ALREADY PAID
    @Test
    void testPaymentAlreadyCompleted() {

        User user = new User();
        user.setId(1L);
        user.setAmount(10000);

        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setStockQuantity(5);
        product.setPrice(5000);

        Order order = new Order();
        order.setUser(user);
        order.setTotalPrice(5000);
        order.setPaymentStatus("SUCCESS");
        order.setProducts(List.of(product));

        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> paymentController.paymentSuccess(1L));

        assertEquals("Payment already completed", ex.getMessage());
    }
}