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
    private BrowseView browseView;
    private Cart cart;
    private User user;

    public LoginController(UserDAO userDAO, LendingMaterialDAO lendingMaterialDAO, AccountDAO accountDAO, 
        LoginView loginView, BrowseView browseView, Cart cart, User user) {

        this.userDAO = userDAO;
        this.lendingMaterialDAO = lendingMaterialDAO;
        this.accountDAO = accountDAO;
        this.cart = cart;
        this.loginView = loginView;
        this.browseView = browseView;
        this.user = User.getInstance();

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
                updateUserFromDB(username, password);
                
                System.out.println("UserID: " + user.getUserId());
                System.out.println("AccountID: " + user.getAccountId());
                System.out.println("Name: " + user.getName());
                System.out.println("Age: " + user.getAge());
                System.out.println("Address: " + user.getAddress());
                System.out.println("Password: " + user.getPassword());
                System.out.println("LibraryCard: " + user.getLibraryCard());
                System.out.println("LoginListener: Cart before creating BrowseController: " + (cart != null));

                loginView.setVisible(false);
                browseView.updateUserDialogue();
                browseView.setVisible(true);
                browseView.debug();
                
            } else {
                System.out.println("Authentication failed for user: " + username);
                loginView.showError("Invalid username or password.");
            }
        }

        private void updateUserFromDB(String username, String password) {
            user.setUserId(username);
            user.setAccountId(userDAO.getUserAccountId(username));
            user.setName(userDAO.getUserName(username));
            user.setAge(userDAO.getUserAge(username));
            user.setAddress(userDAO.getUserAddress(username));
            user.setPassword(password);
            user.setLibraryCard(userDAO.getUserLibraryCardNumber(username));
            user.setType(userDAO.getUserType(username));
        }
    }
}
