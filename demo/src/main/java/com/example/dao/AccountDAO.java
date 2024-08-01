package com.example.dao;

import com.example.models.Account;
import com.example.models.LendingMaterial;
import com.example.models.LibraryCard;
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
    List<Document> getAllReservations();
    void updateLendedItemDaysOverdue(Document item, long daysOverdue);
    void updateLendedItemLastBalanceUpdate(Document item, LocalDate lastBalanceUpdate);
    void incrementAccountBalance(String accountId, double amount);
    boolean returnLendedItem(String materialID, String accountID);
    void reserveItem(String accountId, LendingMaterial lendingMaterial);
    LocalDate getReturnDate(LendingMaterial lendingMaterial);
    LocalDate getAvailableDate(LendingMaterial lendingMaterial);
    LibraryCard getLibraryCard(String accountId);
    Document getLendedItemById(String materialID, String accountId);
    void updateLendedItemReturnDate(String materialID, String accountID, LocalDate newReturnDate);
    Boolean reservationExists(LendingMaterial lendingMaterial);
}