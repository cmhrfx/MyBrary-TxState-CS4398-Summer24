package com.example.view;

import com.example.models.Book;
import com.example.models.Cart;
import com.example.models.LendingMaterial;
import com.example.models.Movie;
import com.example.models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class BrowseView extends JFrame {
    private JTable itemTable;
    private DefaultTableModel tableModel;
    private Cart cart;
    private User user;
    private JButton addButton;
    private JButton checkoutButton;

    public BrowseView(User user) {
        this.user = user;
        this.cart = cart;

        setTitle("Browse Items");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel userInfoLabel = new JLabel("Welcome, " + user.getName());
        add(userInfoLabel, BorderLayout.NORTH);

        String[] columnNames = {"Title", "Author"};
        tableModel = new DefaultTableModel(columnNames, 0);
        itemTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(itemTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        addButton = new JButton("Add to Cart");
        checkoutButton = new JButton("Checkout");
        panel.add(addButton);
        panel.add(checkoutButton);
        add(panel, BorderLayout.SOUTH);

        setVisible(true);
    }

    public void setItems(List<LendingMaterial> items) {
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

    public LendingMaterial getSelectedItem() {
        int selectedRow = itemTable.getSelectedRow();
        if (selectedRow >= 0) {
            String title = (String) tableModel.getValueAt(selectedRow, 0);
            String author = (String) tableModel.getValueAt(selectedRow, 1);
            for (LendingMaterial item : cart.getItems()) {
                if (item instanceof Book) {
                    Book book = (Book) item;
                    if (book.getTitle().equals(title) && book.getAuthor().equals(author)) {
                        return book;
                    }
                } else if (item instanceof Movie) {
                    Movie movie = (Movie) item;
                    if (movie.getTitle().equals(title) && movie.getAuthor().equals(author)) {
                        return movie;
                    }
                }
            }
        }
        return null;
    }

    public void displayMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public void addAddToCartListener(ActionListener listener) {
        addButton.addActionListener(listener);
    }

    public void addCheckoutListener(ActionListener listener) {
        checkoutButton.addActionListener(listener);
    }
}
