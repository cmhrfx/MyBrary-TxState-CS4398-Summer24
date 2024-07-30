package com.example.controller;

import com.example.dao.UserDAO;
import com.example.models.User;
import com.example.view.BrowseView;
import com.example.view.LoginView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginController {
    private UserDAO userDAO;
    private LoginView loginView;

    public LoginController(UserDAO userDAO, LoginView loginView) {
        this.userDAO = userDAO;
        this.loginView = loginView;
        this.loginView.addLoginListener(new LoginListener());

    }

    class LoginListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = loginView.getUsername();
            String password = loginView.getPassword();

            System.out.println("Attempting to authenticate user: " + username);

            // Use UserDAO to authenticate
            boolean authenticated = userDAO.authenticateUser(username, password);
            if (authenticated) {
                System.out.println("User authenticated: " + username);
                loginView.dispose();
                User user = userDAO.getUserById(username); // Assuming username is the user ID
                System.out.println("UserID: " + user.getUserId());
                System.out.println("Account: " + user.getAccountId());
                System.out.println("Name: " + user.getName());
                System.out.println("Age: " + user.getAge());
                System.out.println("Address: " + user.getAddress());
                System.out.println("Password: " + user.getPassword());
                System.out.println("LibraryCard: " + user.getLibraryCard());

                BrowseView browseView = new BrowseView(user);
                browseView.setVisible(true);
            } else {
                System.out.println("Authentication failed for user: " + username);
                loginView.showError("Invalid username or password.");
            }
        }
    }
}
