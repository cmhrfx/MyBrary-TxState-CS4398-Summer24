package com.example.view;

import com.example.dao.LendingMaterialDAO;
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
    private LendingMaterialDAO lendingMaterialDAO;

    public BrowseView(User user, Cart cart, LendingMaterialDAO lendingMaterialDAO) {  // Constructor should accept Cart as a parameter
        this.user = User.getInstance();
        this.cart = cart;
        this.lendingMaterialDAO = lendingMaterialDAO;

        setTitle("Browse Items");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel userInfoLabel = new JLabel("Welcome, " + user.getName());
        add(userInfoLabel, BorderLayout.NORTH);

        String[] columnNames = {"Title", "Author", "Type", "Copies Available"};
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

        populateTable(lendingMaterialDAO.getAllLendingMaterials());
    }

    public void setItems(List<LendingMaterial> items) {
        tableModel.setRowCount(0); // Clear existing data
        for (LendingMaterial item : items) {
                String[] data = {item.getTitle(), 
                    item.getAuthor(), item.getSubType(), String.valueOf(item.getCopiesAvailable())};
                tableModel.addRow(data);
        } 
    }

    public LendingMaterial getSelectedItem() {
        int selectedRow = itemTable.getSelectedRow();
        if (selectedRow >= 0) {
            String title = (String) tableModel.getValueAt(selectedRow, 0);
            System.out.println("Selected Title: " + title);
            String author = (String) tableModel.getValueAt(selectedRow, 1);
            System.out.println("Selected Author: " + author);
            for (LendingMaterial item : lendingMaterialDAO.getAllLendingMaterials()) {
                if (item instanceof Book) {
                    System.out.println("Found a book!");
                    Book book = (Book) item;
                    if (book.getTitle().equals(title) && book.getAuthor().equals(author)) {
                        System.out.println("Returning Book!");
                        return book;
                    }
                } else if (item instanceof Movie) {
                    System.out.println("Found a movie!");
                    Movie movie = (Movie) item;
                    if (movie.getTitle().equals(title) && movie.getAuthor().equals(author)) {
                        System.out.println("Returning Movie!");
                        return movie;
                    }
                } else {
                    System.out.println("Not a book or movie!");
                }
            }
        }
        return null;
    }

    private void populateTable(List<LendingMaterial> items) {
        for (LendingMaterial item : items) {
            String[] data = {item.getTitle(), item.getAuthor(), 
                item.getSubType(), String.valueOf(item.getCopiesAvailable())};
            tableModel.addRow(data);
        }
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

    public void debug(){
        System.out.println("BrowseView:: User: " + user.getName());
    }

}
