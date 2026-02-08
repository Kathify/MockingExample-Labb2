package com.example.shop;

public class Item {
    private final String name;
    private final double price;
    private int quantity;

    public Item(String name, double price, int quantity) {
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative");
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be at least 1");
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("Quantity cannot be negative");
        this.quantity = quantity;
    }
}
