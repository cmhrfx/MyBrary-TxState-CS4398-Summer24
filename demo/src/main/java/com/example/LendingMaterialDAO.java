package com.example;

import com.example.models.LendingMaterial;

public interface LendingMaterialDAO {
    LendingMaterial getLendingMaterialById(String id);
    void insertLendingMaterial(LendingMaterial lendingMaterial);
    void updateLendingMaterial(LendingMaterial lendingMaterial);
    void deleteLendingMaterial(String id);
}
