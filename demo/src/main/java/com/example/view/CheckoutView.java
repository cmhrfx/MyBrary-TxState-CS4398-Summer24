package com.example.view;

import com.example.dao.LendingMaterialDAO;
import com.example.dao.AccountDAO;
import com.example.view.BrowseView;
import com.example.view.LoginView;
import com.example.controller.BrowseController;
import com.example.controller.LoginController;
import com.example.models.Book;
import com.example.models.Cart;
import com.example.models.LendingMaterial;
import com.example.models.Movie;
import com.example.models.User;

import javafx.event.ActionEvent;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class CheckoutView extends JFrame {
    private AccountDAO accountDAO;
    private LendingMaterialDAO lendingMaterialDAO;
    private JTable cartTable;
    private DefaultTableModel tableModel;
    private Cart cart;
    private User user;
    private JButton previousButton;
    private JButton removeButton;
    private JButton confirmButton;
    private BrowseView browseView;

    public CheckoutView(User user, Cart cart, AccountDAO accountDAO, LendingMaterialDAO lendingMaterialDAO, BrowseView browseView) {
        this.user = user;
        this.cart = cart;
        this.lendingMaterialDAO = lendingMaterialDAO;
        this.accountDAO = accountDAO;
        this.browseView = browseView;
        initializeUI();
        
    }

    private void initializeUI(){
        setTitle("Checkout");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel userInfoLabel = new JLabel("User: " + user.getName() + " - Checkout");
        add(userInfoLabel, BorderLayout.NORTH);

        String[] columnNames = {"Title", "Author"};
        tableModel = new DefaultTableModel(columnNames, 0);
        cartTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(cartTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        previousButton = new JButton("Previous");
        removeButton = new JButton("Remove Selected Book");
        confirmButton = new JButton("Confirm Checkout");
        buttonPanel.add(previousButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(confirmButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        setupListeners();
        populateTable(cart.getItems());
    }

    private void setupListeners() {
        removeButton.addActionListener(e -> handleRemove());
        previousButton.addActionListener(e -> handlePrevious());
    }

    private void populateTable(List<LendingMaterial> items) {
        tableModel.setRowCount(0); // Clear existing data
        for (LendingMaterial item : items) {
            if (item instanceof Book) {
                Book book = (Book) item;
                String[] data = {book.getTitle(), book.getAuthor()};
                tableModel.addRow(data);
            } else if (item instanceof Movie) {
                Movie movie = (Movie) item;
                String[] data = {movie.getTitle(), movie.getAuthor()};
                tableModel.addRow(data);
            }
        }
    }

    private void handlePrevious(){
        this.dispose();  // Close CheckoutView
        browseView.setVisible(true);
        //this.dispose();  // Close CheckoutView
    }

    private void handleRemove(){
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow >= 0) {
            LendingMaterial item = cart.getItems().get(selectedRow);
            cart.removeItem(item);
            tableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(null, "Item removed from cart.");
        } else {
            JOptionPane.showMessageDialog(null, "Please select an item to remove.");
        }
    }
    
    public void setCart(List<LendingMaterial> items) {
        populateTable(items);
    }

    public void displayMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public void addConfirmListener(ActionListener listener) {
        for (ActionListener al : confirmButton.getActionListeners()) {
            confirmButton.removeActionListener(al);
        }
        confirmButton.addActionListener(listener);
    }
}

/*removeButton.addActionListener(e -> {
    int selectedRow = cartTable.getSelectedRow();
    if (selectedRow >= 0) {
        LendingMaterial item = cart.getItems().get(selectedRow);
        cart.removeItem(item);
        tableModel.removeRow(selectedRow);
        JOptionPane.showMessageDialog(null, "Item removed from cart.");
    } else {
        JOptionPane.showMessageDialog(null, "Please select an item to remove.");
    }
});*/