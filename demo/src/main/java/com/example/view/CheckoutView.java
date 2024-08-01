package com.example.view;

import com.example.dao.AccountDAO;
import com.example.models.Book;
import com.example.models.Cart;
import com.example.models.LendingMaterial;
import com.example.models.Movie;
import com.example.models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.bson.Document;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

import java.time.LocalDate;


public class CheckoutView extends JFrame {
    private AccountDAO accountDAO;
    private JTable cartTable;
    private DefaultTableModel tableModel;
    private Cart cart;
    private User user;
    private JButton previousButton;
    private JButton removeButton;
    private JButton confirmButton;
    private JLabel userInfoLabel;

    public CheckoutView(User user, Cart cart, AccountDAO accountDAO) {
        this.user = user;
        this.cart = cart;
        this.accountDAO = accountDAO;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Checkout");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        userInfoLabel = new JLabel("User: " + user.getName() + " - Checkout");
        add(userInfoLabel, BorderLayout.NORTH);

        String[] columnNames = {"Title", "Author", "Best Seller", "Return Date"};
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

        removeButton.addActionListener(e -> {
            int selectedRow = cartTable.getSelectedRow();
            if (selectedRow >= 0) {
                LendingMaterial item = cart.getItems().get(selectedRow);
                cart.removeItem(item);
                tableModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(null, "Item removed from cart.");
            } else {
                JOptionPane.showMessageDialog(null, "Please select an item to remove.");
            }
        });

        populateTable(cart.getItems());
    }

    private void populateTable(List<LendingMaterial> items) {
        tableModel.setRowCount(0); // Clear existing data

        for (LendingMaterial item : items) {
            String title = item.getTitle();
            String author = item.getAuthor();
            boolean isBestSeller = false;
            LocalDate returnDate = accountDAO.getReturnDate(item);

            if (item instanceof Book) {
                isBestSeller = ((Book) item).getBestSeller();
            }

            Object[] rowData = {title, author, isBestSeller, returnDate};
            tableModel.addRow(rowData);
        }
    }

    public void updateUserInfo() {
        userInfoLabel.setText("User: " + user.getName());
    }

    public void setCart(List<LendingMaterial> items) {
        populateTable(items);
    }

    public void clearCart() {
        tableModel.setRowCount(0);
    }

    public void addConfirmListener(ActionListener listener) {
        for (ActionListener al : confirmButton.getActionListeners()) {
            confirmButton.removeActionListener(al);
        }
        confirmButton.addActionListener(listener);
    }

    public void addRemoveButtonListener(ActionListener listener) {
        for (ActionListener al : removeButton.getActionListeners()) {
            removeButton.removeActionListener(al);
        }
        removeButton.addActionListener(listener);
    }

    public void addPreviousButtonListener(ActionListener listener) {
        for (ActionListener al : previousButton.getActionListeners()) {
            previousButton.removeActionListener(al);
        }
        previousButton.addActionListener(listener);
    }

    public int getSelectedRow() {
        return cartTable.getSelectedRow();
    }

    public void removeSelectedRow(int row) {
        tableModel.removeRow(row);
    }

    public void displayMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}
