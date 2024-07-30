package com.example.dao;

import com.example.models.User;

public interface UserDAO {
    User getUserById(String userId);
    boolean authenticateUser(String userId, String password);
    void insertUser(User user);
    void updateUser(User user);
    void deleteUser(int userId);
}
