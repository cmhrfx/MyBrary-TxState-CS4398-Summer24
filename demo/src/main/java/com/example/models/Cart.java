package com.example.models;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<LendingMaterial> items;

    public Cart() {
        items = new ArrayList<>();
    }

    public List<LendingMaterial> getItems() {
        return items;
    }

    public void addItem(LendingMaterial item) {
        items.add(item);
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
        int itemCount = 0;
        for (LendingMaterial item : items) {
            itemCount++;
        }
        return itemCount;
    }
}
