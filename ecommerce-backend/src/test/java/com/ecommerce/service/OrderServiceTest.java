package com.ecommerce.service;

import com.ecommerce.entity.*;
import com.ecommerce.repository.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepo;

    @Mock
    private CartRepository cartRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private ProductRepository productRepo; // ✅ IMPORTANT

    @InjectMocks
    private OrderService orderService;

    public OrderServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrder_Success() {

        // 👉 Dummy user
        User user = new User();
        user.setUsername("satish");

        // 👉 Dummy product
        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(1000);
        product.setStockQuantity(10);

        // 👉 Cart item
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setProduct(product);
        cart.setQuantity(2);

        List<Cart> cartList = List.of(cart);

        // 👉 Mock DB
        when(userRepo.findByUsername("satish")).thenReturn(Optional.of(user));
        when(cartRepo.findByUser(user)).thenReturn(cartList);
        when(orderRepo.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        // 👉 Call method
        Order result = orderService.createOrder("satish", "Pune");

        // 👉 Assertions (🔥 IMPORTANT)
        assertNotNull(result);
        assertEquals("Pune", result.getAddress());
        assertEquals("PENDING", result.getPaymentStatus());
        assertEquals(2000, result.getTotalPrice());

        assertEquals(1, result.getItems().size());
        assertEquals(2, result.getItems().get(0).getQuantity());

        // 👉 Verify interactions
        verify(orderRepo).save(any(Order.class));
        verify(cartRepo).deleteAll(cartList);
        verify(productRepo, atLeastOnce()).save(any(Product.class)); // ✅
    }
}