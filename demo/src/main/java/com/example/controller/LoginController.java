package com.example.controller;

import com.example.dao.LendingMaterialDAO;
import com.example.dao.AccountDAO;
import com.example.dao.UserDAO;
import com.example.models.User;
import com.example.models.Cart;
import com.example.view.BrowseView;
import com.example.view.LoginView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginController {
    private UserDAO userDAO;
    private LendingMaterialDAO lendingMaterialDAO;
    private AccountDAO accountDAO;
    private LoginView loginView;
    private Cart cart;

    public LoginController(UserDAO userDAO, LendingMaterialDAO lendingMaterialDAO, AccountDAO accountDAO, 
        LoginView loginView, Cart cart) {

        this.userDAO = userDAO;
        this.lendingMaterialDAO = lendingMaterialDAO;
        this.accountDAO = accountDAO;
        this.cart = cart;
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
                System.out.println("AccountID: " + user.getAccountId());
                System.out.println("Name: " + user.getName());
                System.out.println("Age: " + user.getAge());
                System.out.println("Address: " + user.getAddress());
                System.out.println("Password: " + user.getPassword());
                System.out.println("LibraryCard: " + user.getLibraryCard());

                System.out.println("LoginListener: Cart before creating BrowseController: " + (cart != null));

                BrowseView browseView = new BrowseView(user, cart, lendingMaterialDAO);
                BrowseController browseController = new BrowseController(browseView, user, lendingMaterialDAO, accountDAO, cart);

            } else {
                System.out.println("Authentication failed for user: " + username);
                loginView.showError("Invalid username or password.");
            }
        }
    }
}
