package com.example;

import com.example.models.Account;

public interface AccountDAO {
    Account getAccountById(int accountId);
    void insertAccount(Account account);
    void updateAccount(Account account);
    void deleteAccount(int accountId);
}
