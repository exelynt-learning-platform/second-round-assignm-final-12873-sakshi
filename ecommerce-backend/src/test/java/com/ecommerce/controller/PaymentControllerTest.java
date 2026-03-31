package com.ecommerce.controller;

import com.ecommerce.service.PaymentService;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.entity.Order;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @Mock
    private OrderRepository orderRepo;

    @InjectMocks
    private PaymentController paymentController;

    public PaymentControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ TEST 1: SUCCESS
    @Test
    void testPaymentSuccess() {

        Order order = new Order();
        order.setPaymentStatus("PENDING");
        order.setItems(java.util.List.of(new com.ecommerce.entity.OrderItem()));

        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));
        when(paymentService.processPayment(1L)).thenReturn("SUCCESS");

        Map<String, String> result = paymentController.success(1L);

        assertEquals("SUCCESS", result.get("status"));
        assertEquals("Payment successful", result.get("message"));
    }

    // ❌ TEST 2: FAILED
    @Test
    void testPaymentFail() {

        Order order = new Order();
        order.setPaymentStatus("PENDING");
        order.setItems(java.util.List.of(new com.ecommerce.entity.OrderItem()));

        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));
        when(paymentService.processPayment(1L)).thenReturn("FAILED");

        Map<String, String> result = paymentController.success(1L);

        assertEquals("FAILED", result.get("status"));
        assertEquals("Payment failed", result.get("message"));
    }

    // ❌ TEST 3: ALREADY PAID
    @Test
    void testPaymentAlreadyCompleted() {

        Order order = new Order();
        order.setPaymentStatus("SUCCESS");
        order.setItems(java.util.List.of(new com.ecommerce.entity.OrderItem()));

        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> paymentController.pay(1L));

        assertEquals("Payment already processed", ex.getMessage());
    }
}