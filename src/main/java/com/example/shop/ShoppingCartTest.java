package com.example.shop;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ShoppingCartTest {

    private ShoppingCart cart;
    private Item apple;
    private Item orange;

    @BeforeEach
    void setUp() {
        cart = new ShoppingCart();
        apple = new Item("Apple", 10.0);
        orange = new Item("Orange", 15.0);
    }

    @Test
    void new_cart_should_be_empty() {
        assertThat(cart.getItems()).isEmpty();
        assertThat(cart.getTotalPrice()).isEqualTo(0.0);
    }

    @Test
    void adding_item_should_increase_total_price() {
        cart.addItem(apple, 2);
        assertThat(cart.getTotalPrice()).isEqualTo(20.0);
    }

    @Test
    void removing_item_should_decrease_total_price() {
        cart.addItem(apple, 2);
        cart.addItem(orange, 1);
        cart.removeItem(apple);
        assertThat(cart.getTotalPrice()).isEqualTo(15.0);
    }

    @Test
    void updating_item_quantity_should_change_total_price() {
        cart.addItem(apple, 1);
        cart.updateItemQuantity(apple, 3);
        assertThat(cart.getTotalPrice()).isEqualTo(30.0);
    }

    @Test
    void applying_discount_should_reduce_total_price() {
        cart.addItem(apple, 2);
        cart.applyDiscount(10); // 10% discount
        assertThat(cart.getTotalPrice()).isEqualTo(18.0);
    }

    @Test
    void removing_nonexistent_item_should_throw_exception() {
        assertThatThrownBy(() -> cart.removeItem(apple))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Item not in cart");
    }

    @Test
    void updating_quantity_for_nonexistent_item_should_throw_exception() {
        assertThatThrownBy(() -> cart.updateItemQuantity(apple, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Item not in cart");
    }

    @Test
    void adding_item_with_zero_or_negative_quantity_should_throw_exception() {
        assertThatThrownBy(() -> cart.addItem(apple, 0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> cart.addItem(apple, -3))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
