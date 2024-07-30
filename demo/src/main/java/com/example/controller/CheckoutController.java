package com.example.controller;

import com.example.dao.LendingMaterialDAO;
import com.example.models.Cart;
import com.example.view.CheckoutView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CheckoutController {
    private LendingMaterialDAO lendingMaterialDAO;
    private CheckoutView view;
    private Cart cart;

    public CheckoutController(LendingMaterialDAO lendingMaterialDAO, CheckoutView view, Cart cart) {
        this.lendingMaterialDAO = lendingMaterialDAO;
        this.view = view;
        this.cart = cart;

        view.addConfirmListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cart.clearCart();
                view.displayMessage("Checkout confirmed! Thank you for your purchase.");
                view.dispose();
            }
        });
    }
}
