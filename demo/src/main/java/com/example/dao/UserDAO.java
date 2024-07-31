package com.example.dao;

import com.example.models.User;

public interface UserDAO {
    User getUserById(String userId);
    String getUserAccountId(String userId);
    String getUserName(String userId);
    int getUserAge(String userId);
    String getUserAddress(String userId);
    String getUserLibraryCardNumber(String userId);
    String getUserType(String userId);
    boolean authenticateUser(String userId, String password);
    void insertUser(User user);
    void updateUser(User user);
    void deleteUser(int userId);
}
