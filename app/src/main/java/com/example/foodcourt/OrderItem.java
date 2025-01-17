package com.example.foodcourt;

import java.io.Serializable;

public class OrderItem implements Serializable {
    private String name;
    private double price;
    private int quantity;

    public OrderItem() {
        // Default constructor required for Firebase
    }

    public OrderItem(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
