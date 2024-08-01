package com.example.dao;

import com.example.models.Account;
import com.example.models.LendingMaterial;
import com.example.models.LibraryCard;
import java.util.List;
import java.time.LocalDate;

import org.bson.Document;

public interface AccountDAO {
    // Account specific //
    Account getAccountById(String accountId);
    void insertAccount(Account account);
    void updateAccount(Account account);
    void deleteAccount(int accountId);
    void incrementAccountBalance(String accountId, double amount);
    void setAccountBalance(String accountId, double amount);
    LibraryCard getLibraryCard(String accountId);

    // Lended Items//
    List<Document> getAllLendedItems();
    Document getLendedItemById(String materialID, String accountId);
    LocalDate getReturnDate(LendingMaterial lendingMaterial);
    LocalDate getAvailableDate(LendingMaterial lendingMaterial);
    void updateLendedItems(List<LendingMaterial> items, String accountId);
    void updateLendedItemDaysOverdue(Document item, long daysOverdue);
    void updateLendedItemLastBalanceUpdate(Document item, LocalDate lastBalanceUpdate);
    void updateLendedItemFees();
    void updateAccountBalance();

    // return and renew
    boolean returnLendedItem(String materialID, String accountID);
    void setLendedItemBeenRenewed(String materialID, String accountID, boolean beenRenewed);
    void updateLendedItemReturnDate(String materialID, String accountID, LocalDate newReturnDate);

    // Reservations //
    List<Document> getAllReservations();
    Boolean reservationExists(LendingMaterial lendingMaterial);
    void reserveItem(String accountId, LendingMaterial lendingMaterial);

}