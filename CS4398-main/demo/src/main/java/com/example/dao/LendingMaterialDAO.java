package com.example.dao;

import com.example.models.LendingMaterial;
import java.util.ArrayList;
import java.util.List;

public interface LendingMaterialDAO {
    LendingMaterial getLendingMaterialById(String id);
    List<LendingMaterial> getAllLendingMaterials();
    void insertLendingMaterial(LendingMaterial lendingMaterial);
    void updateLendingMaterial(LendingMaterial lendingMaterial);
    void deleteLendingMaterial(String id);
}
