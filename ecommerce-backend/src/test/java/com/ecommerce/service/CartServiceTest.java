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

    // ✅ TEST 1: ADD NEW ITEM
    @Test
    void testAddToCart_NewItem() {

        User user = new User();
        user.setUsername("satish");

        Product product = new Product();
        product.setId(1L);
        product.setName("Mobile");
        product.setPrice(2000);
        product.setStockQuantity(10);

        when(userRepo.findByUsername("satish")).thenReturn(Optional.of(user));
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepo.findByUserAndProduct(user, product)).thenReturn(Optional.empty());
        when(cartRepo.save(any(Cart.class))).thenAnswer(i -> i.getArgument(0));

        Cart result = cartService.addToCart("satish", 1L, 2);

        assertNotNull(result);
        assertEquals(2, result.getQuantity());
        assertEquals(product, result.getProduct());

        verify(cartRepo).save(any(Cart.class));
    }

    // ✅ TEST 2: UPDATE EXISTING ITEM
    @Test
    void testAddToCart_UpdateExisting() {

        User user = new User();
        user.setUsername("satish");

        Product product = new Product();
        product.setStockQuantity(10);

        Cart existingCart = new Cart();
        existingCart.setQuantity(2);

        when(userRepo.findByUsername("satish")).thenReturn(Optional.of(user));
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepo.findByUserAndProduct(user, product)).thenReturn(Optional.of(existingCart));
        when(cartRepo.save(any(Cart.class))).thenAnswer(i -> i.getArgument(0));

        Cart result = cartService.addToCart("satish", 1L, 3);

        assertEquals(5, result.getQuantity()); // 2 + 3
    }

    // ❌ TEST 3: INSUFFICIENT STOCK
    @Test
    void testAddToCart_InsufficientStock() {

        User user = new User();
        user.setUsername("satish");

        Product product = new Product();
        product.setStockQuantity(1);

        when(userRepo.findByUsername("satish")).thenReturn(Optional.of(user));
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> cartService.addToCart("satish", 1L, 5));

        assertEquals("Insufficient stock", ex.getMessage());
    }

    // ❌ TEST 4: INVALID QUANTITY
    @Test
    void testAddToCart_InvalidQuantity() {

        RuntimeException ex = assertThrows(IllegalArgumentException.class,
                () -> cartService.addToCart("satish", 1L, 0));

        assertEquals("Quantity must be greater than 0", ex.getMessage());
    }
}