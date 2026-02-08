package com.example.shop;

import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

public class ShoppingCart {

    private final Map<String, Item> items = new HashMap<>();

    public boolean addItem(String name, double price, int quantity) {
        if (price < 0 || quantity <= 0) return false;

        if (items.containsKey(name)) {
            Item existingItem = items.get(name);
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            items.put(name, new Item(name, price, quantity));
        }
        return true;
    }

    public boolean removeItem(String name) {
        return items.remove(name) != null;
    }

    public double totalPrice() {
        return items.values().stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();
    }

    public void applyDiscount(double percent) {
        if (percent <= 0 || percent > 100) return;
        for (Item item : items.values()) {
            item.setPrice(item.getPrice() * (1 - percent / 100));
        }
    }

    public Map<String, Item> getItems() {
        return Collections.unmodifiableMap(items);
    }

    public static class Item {
        private final String name;
        private double price;
        private int quantity;

        public Item(String name, double price, int quantity) {
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}
