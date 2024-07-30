package com.example.controller;

import com.example.models.LendingMaterial;
import com.example.dao.LendingMaterialDAO;
import com.example.models.Book;
import com.example.models.Cart;
import com.example.models.User;
import com.example.view.BrowseView;
import com.example.view.CheckoutView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class BrowseController {
    private LendingMaterialDAO lendingMaterialDAO;
    private Cart cart;
    private BrowseView view;
    private User user;

    public BrowseController(LendingMaterialDAO lendingMaterialDAO, Cart cart, BrowseView view, User user) {
        this.lendingMaterialDAO = lendingMaterialDAO;
        this.cart = cart;
        this.view = view;
        this.user = user;

        // Use DAO to fetch lending materials
        List<LendingMaterial> materials = lendingMaterialDAO.getAllLendingMaterials();
        if (materials != null)
        {
            System.out.println("BrowseController: Lending Materials populated");
        } else {
            System.out.println("BrowseController: Failed to populate Lending Materials");
        }

        view.setItems(materials);

        view.addAddToCartListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LendingMaterial selectedItem = view.getSelectedItem();
                if (selectedItem != null) {
                    cart.addItem(selectedItem);
                    view.displayMessage("Item added to cart!");
                } else {
                    view.displayMessage("No item selected!");
                }
            }
        });

        view.addCheckoutListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.setVisible(false);
                CheckoutView checkoutView = new CheckoutView(user, cart);
                new CheckoutController(lendingMaterialDAO, checkoutView, cart);
                checkoutView.setCart(cart.getItems());
                checkoutView.setVisible(true);
            }
        });
    }
}
