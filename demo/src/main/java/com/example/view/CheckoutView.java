package com.example.view;

import com.example.models.Book;
import com.example.models.Cart;
import com.example.models.LendingMaterial;
import com.example.models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class CheckoutView extends JFrame {
    private JTable cartTable;
    private DefaultTableModel tableModel;
    private Cart cart;
    private User user;
    private JButton confirmButton;

    public CheckoutView(User user, Cart cart) {
        this.user = user;
        this.cart = cart;

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
        confirmButton = new JButton("Confirm Checkout");
        JButton removeButton = new JButton("Remove Selected Book");
        buttonPanel.add(confirmButton);
        buttonPanel.add(removeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        confirmButton.addActionListener(e -> {
            if (cart.getBooks().isEmpty()) {
                JOptionPane.showMessageDialog(null, "No items in cart to checkout.");
            } else {
                cart.clearCart(); // Clear the cart after successful checkout
                JOptionPane.showMessageDialog(null, "Checkout successful!");
                dispose();
            }
        });

        removeButton.addActionListener(e -> {
            int selectedRow = cartTable.getSelectedRow();
            if (selectedRow >= 0) {
                LendingMaterial item = cart.getBooks().get(selectedRow);
                cart.removeItem(item);
                tableModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(null, "Book removed from cart.");
            } else {
                JOptionPane.showMessageDialog(null, "Please select a book to remove.");
            }
        });

        populateTable(cart.getBooks());
    }

    private void populateTable(List<LendingMaterial> items) {
        tableModel.setRowCount(0); // Clear existing data
        for (LendingMaterial item : items) {
            if (item instanceof Book) {
                Book book = (Book) item;
                String[] data = {book.getTitle(), book.getAuthor()};
                tableModel.addRow(data);
            }
        }
    }

    public void setCart(List<LendingMaterial> items) {
        populateTable(items);
    }

    public void displayMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public void addConfirmListener(ActionListener listener) {
        confirmButton.addActionListener(listener);
    }
}
