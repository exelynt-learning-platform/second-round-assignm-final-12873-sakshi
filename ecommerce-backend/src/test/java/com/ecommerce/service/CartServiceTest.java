package com.ecommerce.service;

import com.ecommerce.entity.*;
import com.ecommerce.repository.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CartServiceTest {

    @Mock
    private CartRepository cartRepo;

    @Mock
    private ProductRepository productRepo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private CartService cartService;

    public CartServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddToCart() {

        // 👉 Dummy user
        User user = new User();
        user.setUsername("satish");

        // 👉 Dummy product
        Product product = new Product();
        //product.setId(1L);

        // 👉 Mock DB
        when(userRepo.findByUsername("satish")).thenReturn(Optional.of(user));
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepo.findByUserAndProduct(user, product)).thenReturn(Optional.empty());

        Cart savedCart = new Cart();
        when(cartRepo.save(any(Cart.class))).thenReturn(savedCart);

        // 👉 Call method
        Cart result = cartService.addToCart("satish", 1L, 2);

        // 👉 Verify
        assertNotNull(result);
        verify(cartRepo).save(any(Cart.class));
    }
}