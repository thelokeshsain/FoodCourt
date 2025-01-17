package com.example.foodcourt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cart implements Serializable {
    private Map<String, FoodItem> items; // Use Map for easy item management

    public Cart() {
        items = new HashMap<>();
    }

    public void addItemToCart(FoodItem foodItem) {
        if (items.containsKey(foodItem.getId())) {
            FoodItem existingItem = items.get(foodItem.getId());
            existingItem.setQuantity(existingItem.getQuantity() + foodItem.getQuantity());
        } else {
            foodItem.setQuantity(foodItem.getQuantity()); // Set quantity before adding
            items.put(foodItem.getId(), foodItem);
        }
    }

    public void removeItemFromCart(FoodItem foodItem) {
        items.remove(foodItem.getId());
    }

    public List<FoodItem> getItems() {
        return new ArrayList<>(items.values());
    }
}
