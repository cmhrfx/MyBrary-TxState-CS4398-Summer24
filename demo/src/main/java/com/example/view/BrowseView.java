package com.example.view;

import com.example.dao.AccountDAO;
import com.example.dao.LendingMaterialDAO;
import com.example.models.Account;
import com.example.models.Book;
import com.example.models.Cart;
import com.example.models.LendingMaterial;
import com.example.models.Movie;
import com.example.models.Journal;
import com.example.models.Magazine;
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
    private JButton logoutButton;
    private AccountDAO accountDAO;  // Add this line

    ////////CHATGPT -CHANGES YOU GAVE
    private JButton myAccountButton;
//////////////

    private LendingMaterialDAO lendingMaterialDAO;
    private JLabel userInfoLabel;

    public BrowseView(User user, Cart cart, LendingMaterialDAO lendingMaterialDAO, AccountDAO accountDAO) {  // Add AccountDAO as a parameter
        this.user = User.getInstance();  // Assuming this should actually use the passed 'user' parameter
        this.cart = cart;
        this.lendingMaterialDAO = lendingMaterialDAO;
        this.accountDAO = accountDAO;  // Initialize the DAO

        initializeUI();
    }

    private void initializeUI() {
        setTitle("Browse Items");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        userInfoLabel = new JLabel("Welcome, " + user.getName());
        add(userInfoLabel, BorderLayout.NORTH);

        String[] columnNames = {"Title", "Author", "Type", "Copies Available"};
        tableModel = new DefaultTableModel(columnNames, 0);
        itemTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(itemTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        addButton = new JButton("Add to Cart");
        checkoutButton = new JButton("Checkout");
        myAccountButton = new JButton("My Account");
        logoutButton = new JButton("Logout");
        panel.add(logoutButton);
        panel.add(myAccountButton);
        panel.add(addButton);
        panel.add(checkoutButton);
        

        add(panel, BorderLayout.SOUTH);

        populateTable(lendingMaterialDAO.getAllLendingMaterials());

        myAccountButton.addActionListener(e -> displayMyAccountGUI());
    }



/* 
    public BrowseView(User user, Cart cart, LendingMaterialDAO lendingMaterialDAO) {  // Constructor should accept Cart as a parameter
        this.user = User.getInstance();
        this.cart = cart;
        this.lendingMaterialDAO = lendingMaterialDAO;

        setTitle("Browse Items");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        userInfoLabel = new JLabel("Welcome, " + user.getName());
        add(userInfoLabel, BorderLayout.NORTH);

        String[] columnNames = {"Title", "Author", "Type", "Copies Available"};
        tableModel = new DefaultTableModel(columnNames, 0);
        itemTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(itemTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        addButton = new JButton("Add to Cart");
        checkoutButton = new JButton("Checkout");


        /////////////////////CHATGPT -CHANGES YOU GAVE
        myAccountButton = new JButton("My Account");
        panel.add(myAccountButton);
        myAccountButton.addActionListener(e -> displayMyAccountGUI());
///////////////////////////

        panel.add(addButton);
        panel.add(checkoutButton);
        add(panel, BorderLayout.SOUTH);

        populateTable(lendingMaterialDAO.getAllLendingMaterials());
    }
*/
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
                } else if (item instanceof Journal) {
                    Journal journal = (Journal) item;
                    if (journal.getTitle().equals(title) && journal.getAuthor().equals(author)) {
                        System.out.println("Not a book or movie!");
                        return journal;
                    }
                } else if (item instanceof Magazine) {
                    Magazine magazine = (Magazine) item;
                    if (magazine.getTitle().equals(title) && magazine.getAuthor().equals(author)) {
                        System.out.println("Not a book or movie!");
                        return magazine;
                    }
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

    ///////////////////////////CHATGPT -CHANGES YOU GAVE
    private void displayMyAccountGUI() {
        Account account = accountDAO.getAccountById(user.getAccountId());
        new MyAccountView(account, accountDAO).setVisible(true);
    }
///////////////////////////////////


    public void displayMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public void addLogoutListener(ActionListener listener) {
        logoutButton.addActionListener(listener);
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

    public void updateUserDialogue(){
        userInfoLabel.setText("Welcome, " + user.getName());
    }

}
