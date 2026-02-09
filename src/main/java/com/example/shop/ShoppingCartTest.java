package com.example.shop;

import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.assertThat;

public class ShoppingCartTest {

    ShoppingCart cart;

    @BeforeEach
    void setUp() {
        cart = new ShoppingCart();
    }

    @Test
    @DisplayName("Add item increases cart size and total price correctly")
    void addItemTest() {
        boolean added = cart.addItem("Apple", 10.0, 2);
        assertThat(added).isTrue();
        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.totalPrice()).isEqualTo(20.0);
    }

    @Test
    @DisplayName("Adding same item updates quantity correctly")
    void addSameItemUpdatesQuantity() {
        cart.addItem("Apple", 10.0, 2);
        cart.addItem("Apple", 10.0, 3);
        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().get("Apple").getQuantity()).isEqualTo(5);
        assertThat(cart.totalPrice()).isEqualTo(50.0);
    }

    @Test
    @DisplayName("Removing an item decreases cart size")
    void removeItemTest() {
        cart.addItem("Apple", 10.0, 2);
        boolean removed = cart.removeItem("Apple");
        assertThat(removed).isTrue();
        assertThat(cart.getItems()).isEmpty();
    }

    @Test
    @DisplayName("Removing a non-existing item returns false")
    void removeNonExistingItem() {
        boolean removed = cart.removeItem("Banana");
        assertThat(removed).isFalse();
    }

    @Test
    @DisplayName("Applying discount reduces total price correctly")
    void applyDiscountTest() {
        cart.addItem("Apple", 10.0, 2);
        cart.addItem("Banana", 5.0, 4); // Total 10 + 20 = 30
        cart.applyDiscount(50); // 50% rabatt
        assertThat(cart.totalPrice()).isEqualTo(15.0);
    }

    @Test
    @DisplayName("Applying invalid discount does not change total price")
    void applyInvalidDiscountTest() {
        cart.addItem("Apple", 10.0, 2);
        double originalTotal = cart.totalPrice();
        cart.applyDiscount(0);
        assertThat(cart.totalPrice()).isEqualTo(originalTotal);

        cart.applyDiscount(150); // >100%
        assertThat(cart.totalPrice()).isEqualTo(originalTotal);
    }

    @Test
    @DisplayName("Adding item with negative price or quantity returns false")
    void addItemInvalidValues() {
        assertThat(cart.addItem("Apple", -10.0, 2)).isFalse();
        assertThat(cart.addItem("Apple", 10.0, -2)).isFalse();
        assertThat(cart.getItems()).isEmpty();
    }

    @Test
    @DisplayName("Total price with multiple items is calculated correctly")
    void totalPriceMultipleItems() {
        cart.addItem("Apple", 10.0, 2);
        cart.addItem("Banana", 5.0, 3); // Total 15
        assertThat(cart.totalPrice()).isEqualTo(35.0);
    }
}