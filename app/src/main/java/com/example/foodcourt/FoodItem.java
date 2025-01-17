package com.example.foodcourt;

import java.io.Serializable;

public class FoodItem implements Serializable {
    private String id;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private int quantity;

    // Default constructor
    public FoodItem() {
        // Default constructor required for calls to DataSnapshot.getValue(FoodItem.class)
        this.quantity = 0;
    }

    // Constructor for admin functionalities
    public FoodItem(String id, String name, String description, double price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.quantity = 0;
    }

    // Constructor for user functionalities
    public FoodItem(String id, String name, String description, double price, int quantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
