package com.example.controller;

import com.example.dao.LendingMaterialDAO;
import com.example.dao.AccountDAO;
import com.example.models.Account;
import com.example.models.Cart;
import com.example.models.User;
import com.example.models.LendingMaterial;
import com.example.view.BrowseView;
import com.example.view.CheckoutView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class CheckoutController {
    private LendingMaterialDAO lendingMaterialDAO;
    private AccountDAO accountDAO;
    private BrowseView browseView;
    private CheckoutView view;
    private Cart cart;
    private User user;

    public CheckoutController(LendingMaterialDAO lendingMaterialDAO, BrowseView browseView, CheckoutView view, Cart cart, User user, AccountDAO accountDAO) {
        this.lendingMaterialDAO = lendingMaterialDAO;
        this.browseView = browseView;
        this.view = view;
        this.cart = Cart.getInstance();
        this.user = User.getInstance();
        this.accountDAO = accountDAO;

        setupListeners();
    }

    private void setupListeners() {
        view.addConfirmListener(e -> handleConfirm());
        view.addPreviousButtonListener(e -> handlePrevious());
    }

    private void handleConfirm() {
        if (canCheckout()) {
            updateAccountWithCheckedOutItems();
            cart.clearCart();
            view.displayMessage("Checkout confirmed! Thank you for your purchase.");
            view.dispose();
        } else {
            view.displayMessage("You do not qualify to checkout this number of items.");
        }
    }

    private void handlePrevious() {
        view.setVisible(false);
        browseView.setVisible(true);
    }

    private boolean canCheckout() {
        // see if they already have checked out items
        Account account = accountDAO.getAccountById(user.getAccountId());
        int oldItems = account.getCheckedOutItems().size();
        System.out.println("Number of old items: " + oldItems);
        System.out.println("Number of new items: " + cart.getItems().size());

        System.out.println("CheckoutController: canCheckout method called.");
        int maxItems = 0;
        if (user.getType().equalsIgnoreCase("member")) {
            if (user.getAge() < 12) {
                maxItems = 5;
            } else {
                maxItems = 8;
            }
        } else if (user.getType().equalsIgnoreCase("staff")) {
            maxItems = 12;
        }
        System.out.println("return case for checkout?: " + ((cart.getItems().size() + oldItems) <= maxItems));
        return ((cart.getItems().size() + oldItems) <= maxItems);
        // return cart.getItems().size() <= maxItems;
    }

    private void updateAccountWithCheckedOutItems() {
        System.out.println("CheckoutController: updateAccount method called.");
        Account account = accountDAO.getAccountById(user.getAccountId());
        if (account != null) {
            System.out.println("CheckoutController:: updateAccount: account found!");
            List<LendingMaterial> checkedOutItems = account.getCheckedOutItems();
            System.out.println("CheckoutController:: updateAccount: items1: " + account.getCheckedOutItems());
            checkedOutItems.addAll(cart.getItems());
            System.out.println("CheckoutController:: updateAccount: items2: " + checkedOutItems);
            account.setCheckedOutItems(checkedOutItems);
            System.out.println("CheckoutController:: updateAccount: items3: " + account.getCheckedOutItems());
            accountDAO.updateAccount(account);
            accountDAO.updateLendedItems(cart.getItems(), user.getAccountId());
            System.out.println("CheckoutController:: updateAccount: account updated!");
        } else {
            System.out.println("CheckoutController:: updateAccount: account null!");
        }
    }
}
