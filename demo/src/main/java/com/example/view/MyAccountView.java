package com.example.view;

import com.example.dao.AccountDAO;
import com.example.models.Account;
import com.example.models.LendingMaterial;
import org.bson.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MyAccountView extends JFrame {
    private Account account;
    private AccountDAO accountDAO;

    public MyAccountView(Account account, AccountDAO accountDAO) {
        this.account = account;
        this.accountDAO = accountDAO;

        setTitle("My Account");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Display user info and balance
        JLabel balanceLabel = new JLabel("Balance: $" + account.getBalance());
        add(balanceLabel, BorderLayout.NORTH);

        // Table to display checked out items
        String[] columnNames = {"Title", "Author", "Type", "Due Date"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable itemTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(itemTable);
        add(scrollPane, BorderLayout.CENTER);

        // Populate the table with checked out items
        populateTable(tableModel, account.getCheckedOutItems());

        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(closeButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void populateTable(DefaultTableModel tableModel, List<LendingMaterial> items) {
        List<Document> allLendedItems = accountDAO.getAllLendedItems(); // Get all lended items from the database

        tableModel.setRowCount(0); // Clear existing data

        for (LendingMaterial item : items) {
            // Initialize the due date string to "No return date"
            String dueDateString = "No return date";

            for (Document lendedItem : allLendedItems) {
                String lendedMaterialID = lendedItem.getString("MaterialID");
                String lendedAccountID = lendedItem.getString("AccountID");

                // Check if the current item's ID and account ID match the lended item's details
                if (lendedMaterialID.equals(item.getMaterialID()) && lendedAccountID.equals(account.getAccountId())) {
                    dueDateString = lendedItem.getString("ReturnDate");
                    break; // Found the matching document, no need to check further
                }
            }

            String[] data = {
                item.getTitle(),
                item.getAuthor(),
                item.getSubType(),
                dueDateString  // Use the found due date string or the default "No return date"
            };
            tableModel.addRow(data);
        }
    }


}
