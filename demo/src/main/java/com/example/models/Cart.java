package com.example.models;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private static Cart instance;
    private List<LendingMaterial> items;

    // Private constructor to prevent instantiation
    private Cart() {
        items = new ArrayList<>();
    }

    // Public method to provide access to the singleton instance
    public static synchronized Cart getInstance() {
        if (instance == null) {
            instance = new Cart();
        }
        return instance;
    }

    public List<LendingMaterial> getItems() {
        return items;
    }

    public void addItem(LendingMaterial item) {
        items.add(item);
    }

    public void setItems(List<LendingMaterial> items) {
        this.items = items;
    }

    public void removeItem(LendingMaterial item) {
        items.remove(item);
    }

    public void clearCart() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    // Count the number of items in the cart
    public int getNumberOfItems() {
        return items.size();
    }
}
