package com.ecommerce.service;

import com.ecommerce.entity.*;
import com.ecommerce.repository.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepo;

    @Mock
    private CartRepository cartRepo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private OrderService orderService;

    public OrderServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrder() {

        // 👉 Step 1: Dummy user
        User user = new User();
        user.setUsername("satish");

        // 👉 Step 2: Dummy product (🔥 FIXED)
        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");          // ✅ REQUIRED
        product.setPrice(1000);
        product.setStockQuantity(10);       // ✅ REQUIRED (>0)

        // 👉 Step 3: Cart item
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setProduct(product);
        cart.setQuantity(2);

        List<Cart> cartList = List.of(cart);

        // 👉 Step 4: Mock DB calls
        when(userRepo.findByUsername("satish")).thenReturn(Optional.of(user));
        when(cartRepo.findByUser(user)).thenReturn(cartList);

        Order savedOrder = new Order();
        when(orderRepo.save(any(Order.class))).thenReturn(savedOrder);

        // 👉 Step 5: Call method
        Order result = orderService.createOrder("satish", "Pune");

        // 👉 Step 6: Verify
        assertNotNull(result);

        verify(orderRepo).save(any(Order.class));
        verify(cartRepo).deleteAll(cartList);
    }
}