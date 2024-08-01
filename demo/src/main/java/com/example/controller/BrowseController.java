package com.example.controller;

import com.example.dao.LendingMaterialDAO;
import com.example.dao.AccountDAO;
import com.example.models.Account;
import com.example.models.Book;
import com.example.models.Cart;
import com.example.models.LendingMaterial;
import com.example.models.Movie;
import com.example.models.User;
import com.example.view.LoginView;
import com.example.view.BrowseView;
import com.example.view.CheckoutView;

import java.util.List;

public class BrowseController {
    private LendingMaterialDAO lendingMaterialDAO;
    private AccountDAO accountDAO;
    private Cart cart;
    private LoginView loginView;
    private BrowseView view;
    private CheckoutView checkoutView;
    private User user;

    public BrowseController(LoginView loginView, BrowseView view,
            CheckoutView checkoutView, User user, LendingMaterialDAO lendingMaterialDAO,
            AccountDAO accountDAO, Cart cart) {
        this.view = view;
        this.checkoutView = checkoutView;
        this.loginView = loginView;
        this.user = User.getInstance();
        this.lendingMaterialDAO = lendingMaterialDAO;
        this.accountDAO = accountDAO;
        this.cart = Cart.getInstance();

        System.out.println("BrowseController: Cart received: " + (this.cart != null));

        List<LendingMaterial> materials = lendingMaterialDAO.getAllLendingMaterials();
        System.out.println("BrowseController: " + (materials != null ? "Lending Materials populated" : "Failed to populate Lending Materials"));
        view.setItems(materials);

        setupListeners();
    }

    private void setupListeners() {
        view.addAddToCartListener(e -> handleAddToCart());
        view.addCheckoutListener(e -> handleCheckout());
        view.addLogoutListener(e -> handleLogout());
    }

    private void handleLogout(){
        cart.clearCart();
        checkoutView.clearCart();
        view.setVisible(false);
        loginView.setVisible(true);
    }

    private void handleAddToCart() {
        LendingMaterial selectedItem = view.getSelectedItem();

        if (selectedItem.getCopiesAvailable() == 0) {
            view.displayMessage("Not enough copies available!", true, () -> handleReserve(selectedItem));
            return;
        }

        if (selectedItem != null) {
            if (selectedItem instanceof Book || selectedItem instanceof Movie) {
                int totalItems = cart.getNumberOfItems();
                if (user.getType().equalsIgnoreCase("Member")) {
                    if (user.getAge() <= 12) {
                        if (totalItems < 5) { // Limit of 5 items for users 12 and under
                            cart.addItem(selectedItem);
                            view.displayMessage("Item added to cart!", false, null);
                            System.out.println("Added item to cart, MaterialID: " + selectedItem.getMaterialID());
                        } else {
                            view.displayMessage("Maximum limit of 5 items reached!", false, null);
                        }
                    } else {
                        if (totalItems < 8) { // Limit of 8 items for members over the age of 12
                            cart.addItem(selectedItem);
                            view.displayMessage("Item added to cart!", false, null);
                            System.out.println("Added item to cart, MaterialID: " + selectedItem.getMaterialID());
                        } else {
                            view.displayMessage("Maximum limit of 8 items reached!", false, null);
                        }
                    }
                } else if (user.getType().equalsIgnoreCase("Staff")) {
                    if (totalItems < 12) { // Limit of 12 items for staff
                        cart.addItem(selectedItem);
                        view.displayMessage("Item added to cart!", false, null);
                        System.out.println("Added item to cart, MaterialID: " + selectedItem.getMaterialID());
                    } else {
                        view.displayMessage("Maximum limit of 12 items reached!", false, null);
                    }
                }
            } else {
                view.displayMessage("Only books and movies can be added to the cart!", false, null);
            }
        } else {
            view.displayMessage("No item selected!", false, null);
        }
    }

    private void handleReserve(LendingMaterial item) {
        // Implement your reservation logic here
        System.out.println("Reserving item with MaterialID: " + item.getMaterialID());
        accountDAO.reserveItem(user.getAccountId(), item);
        view.displayMessage("Item has been reserved!", false, null);
    }
        
    private void handleCheckout() {
        checkoutView.setCart(cart.getItems());
        checkoutView.updateUserInfo();
        view.setVisible(false);
        checkoutView.setVisible(true);
    }
}
