package com.example.controller;

import com.example.dao.LendingMaterialDAO;
import com.example.dao.AccountDAO;
import com.example.models.Account;
import com.example.models.Cart;
import com.example.models.User;
import com.example.models.LendingMaterial;
import com.example.view.CheckoutView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CheckoutController {
    private LendingMaterialDAO lendingMaterialDAO;
    private AccountDAO accountDAO;
    private CheckoutView view;
    private Cart cart;
    private User user;

    public CheckoutController(LendingMaterialDAO lendingMaterialDAO, CheckoutView view, Cart cart, User user, AccountDAO accountDAO) {
        this.lendingMaterialDAO = lendingMaterialDAO;
        this.view = view;
        this.cart = cart;
        this.user = user;
        this.accountDAO = accountDAO;

        view.addConfirmListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (canCheckout()) {
                    updateAccountWithCheckedOutItems();
                    cart.clearCart();
                    view.displayMessage("Checkout confirmed! Thank you for your purchase.");
                    view.dispose();
                } else {
                    view.displayMessage("You do not qualify to checkout this number of items.");
                }
            }
        });
    }

    private boolean canCheckout() {
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
        return cart.getItems().size() <= maxItems;
    }

    private void updateAccountWithCheckedOutItems() {
        System.out.println("CheckoutController: updateAccount method called.");
        Account account = accountDAO.getAccountById(user.getAccountId());
        if (account != null) {
            System.out.println("CheckoutController:: updateAccount: account found!");
            List<LendingMaterial> checkedOutItems = account.getCheckedOutItems();
            checkedOutItems.addAll(cart.getItems());
            account.setCheckedOutItems(checkedOutItems);
            accountDAO.updateAccount(account);
            accountDAO.updateLendedItems(cart.getItems(), user.getAccountId());
            System.out.println("CheckoutController:: updateAccount: account updated!");
        } else {
            System.out.println("CheckoutController:: updateAccount: account null!");
        }
    }
}