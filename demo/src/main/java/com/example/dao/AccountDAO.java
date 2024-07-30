package com.example.dao;

import com.example.models.Account;

public interface AccountDAO {
    Account getAccountById(String accountId);
    void insertAccount(Account account);
    void updateAccount(Account account);
    void deleteAccount(int accountId);
}
