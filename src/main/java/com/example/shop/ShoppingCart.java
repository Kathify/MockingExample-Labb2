package com.example.shop;

import java.util.HashMap;
import java.util.Map;

public class ShoppingCart {

    private final Map<Item, Integer> items = new HashMap<>();
    private double discount = 0.0;

    public void addItem(Item item, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        items.put(item, items.getOrDefault(item, 0) + quantity);
    }

    public void removeItem(Item item) {
        if (!items.containsKey(item)) throw new IllegalArgumentException("Item not in cart");
        items.remove(item);
    }

    public void updateItemQuantity(Item item, int quantity) {
        if (!items.containsKey(item)) throw new IllegalArgumentException("Item not in cart");
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        items.put(item, quantity);
    }

    public void applyDiscount(double percent) {
        if (percent < 0 || percent > 100) throw new IllegalArgumentException("Invalid discount");
        discount = percent;
    }

    public double getTotalPrice() {
        double total = items.entrySet().stream()
                .mapToDouble(e -> e.getKey().getPrice() * e.getValue())
                .sum();
        return total * (1 - discount / 100);
    }

    public Map<Item, Integer> getItems() {
        return Map.copyOf(items);
    }
}
