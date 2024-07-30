package com.example.controller;

import com.example.dao.LendingMaterialDAO;
import com.example.dao.AccountDAO;
import com.example.models.Book;
import com.example.models.Cart;
import com.example.models.LendingMaterial;
import com.example.models.User;
import com.example.view.BrowseView;
import com.example.view.CheckoutView;

import java.util.List;

public class BrowseController {
    private LendingMaterialDAO lendingMaterialDAO;
    private AccountDAO accountDAO;
    private Cart cart;
    private BrowseView view;
    private User user;

    public BrowseController(BrowseView view, User user, LendingMaterialDAO lendingMaterialDAO, AccountDAO accountDAO, Cart cart) {
        this.view = view;
        this.user = user;
        this.lendingMaterialDAO = lendingMaterialDAO;
        this.accountDAO = accountDAO;
        this.cart = cart;

        System.out.println("BrowseController: Cart received: " + (this.cart != null));

        List<LendingMaterial> materials = lendingMaterialDAO.getAllLendingMaterials();
        System.out.println("BrowseController: " + (materials != null ? "Lending Materials populated" : "Failed to populate Lending Materials"));
        view.setItems(materials);

        setupListeners();
    }

    private void setupListeners() {
        view.addAddToCartListener(e -> handleAddToCart());
        view.addCheckoutListener(e -> handleCheckout());
    }

    private void handleAddToCart() {
        LendingMaterial selectedItem = view.getSelectedItem();
        if (selectedItem != null && selectedItem instanceof Book) {
            // Check if the user is 12 or under
            if (user.getAge() <= 12) {
                if (cart.getNumberOfBooks() < 5) { // Limit of 5 books for users 12 and under
                    cart.addItem(selectedItem);
                    view.displayMessage("Item added to cart!");
                    System.out.println("Added item to cart, MaterialID: " + selectedItem.getMaterialID());
                } else {
                    view.displayMessage("Maximum limit of 5 books reached!");
                }
            } else {
                // No limit for users 13 and up
                cart.addItem(selectedItem);
                view.displayMessage("Item added to cart!");
                System.out.println("Added item to cart, MaterialID: " + selectedItem.getMaterialID());
            }
        } else {
            view.displayMessage("No item selected or item is not a book!");
        }
    }
    

    private void handleCheckout() {
        CheckoutView checkoutView = new CheckoutView(user, cart, accountDAO);
        new CheckoutController(lendingMaterialDAO, checkoutView, cart);
        checkoutView.setCart(cart.getItems());
        view.setVisible(false);
        checkoutView.setVisible(true);
    }
}




/*package com.example.controller;

import com.example.dao.LendingMaterialDAO;
import com.example.dao.AccountDAO;
import com.example.models.Book;
import com.example.models.Cart;
import com.example.models.LendingMaterial;
import com.example.models.User;
import com.example.view.BrowseView;
import com.example.view.CheckoutView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class BrowseController {
    private LendingMaterialDAO lendingMaterialDAO;
    private AccountDAO accountDAO;
    private Cart cart;
    private BrowseView view;
    private User user;

    public BrowseController(BrowseView view, User user, LendingMaterialDAO lendingMaterialDAO, AccountDAO accountDAO, Cart cart) {
        this.view = view;
        this.user = user;
        this.lendingMaterialDAO = lendingMaterialDAO;
        this.accountDAO = accountDAO;
        this.cart = cart;

        System.out.println("BrowseController: Cart received: " + (this.cart != null));

        // Use DAO to fetch lending materials
        List<LendingMaterial> materials = lendingMaterialDAO.getAllLendingMaterials();
        if (materials != null) {
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
                    System.out.println("Added item to cart, MaterialID: " + selectedItem.getMaterialID());
                } else {
                    view.displayMessage("No item selected!");
                }
            }
        });

        view.addCheckoutListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.setVisible(false);
                CheckoutView checkoutView = new CheckoutView(user, cart, accountDAO);
                new CheckoutController(lendingMaterialDAO, checkoutView, cart);
                checkoutView.setCart(cart.getItems());
                checkoutView.setVisible(true);
            }
        });
    }
}

*/
