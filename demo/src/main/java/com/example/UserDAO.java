package com.example;

public interface UserDAO {
    User getUserById(String userId);
    boolean authenticateUser(String email, String password);
    void insertUser(User user);
    void updateUser(User user);
    void deleteUser(String userId);
}
