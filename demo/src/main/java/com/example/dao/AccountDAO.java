package com.example.dao;

import com.example.models.Account;
import com.example.models.LendingMaterial;
import java.util.List;
import java.time.LocalDate;

import org.bson.Document;

public interface AccountDAO {
    Account getAccountById(String accountId);
    void insertAccount(Account account);
    void updateAccount(Account account);
    void deleteAccount(int accountId);
    void updateLendedItems(List<LendingMaterial> items, String accountId);
    List<Document> getAllLendedItems();
    void updateLendedItemDaysOverdue(Document item, long daysOverdue);
    void updateLendedItemLastBalanceUpdate(Document item, LocalDate lastBalanceUpdate);
    void incrementAccountBalance(String accountId, double amount);
}