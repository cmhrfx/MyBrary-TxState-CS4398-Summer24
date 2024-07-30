package com.example.models;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<LendingMaterial> items;

    public Cart() {
        items = new ArrayList<>();
    }

    public List<LendingMaterial> getBooks() {
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
}
