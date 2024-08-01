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
    private JTable itemTable; // Declare the JTable at the class level

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
    
        // Initialize itemTable
        String[] columnNames = {"MaterialID", "Title", "Author", "Type", "Due Date"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        itemTable = new JTable(tableModel);
                JScrollPane scrollPane = new JScrollPane(itemTable);
        add(scrollPane, BorderLayout.CENTER);
    
        // Populate the table
        populateTable(tableModel, account.getCheckedOutItems());
    
        // Setup buttons
        setupButtons();
    
        setVisible(true);
    }
    private void setupButtons() {
        JPanel bottomPanel = new JPanel();
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        bottomPanel.add(closeButton);
    
        JButton returnButton = new JButton("Return");
        returnButton.addActionListener(e -> returnSelectedItem());
        bottomPanel.add(returnButton);
    
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
                item.getMaterialID(),
                item.getTitle(),
                item.getAuthor(),
                item.getSubType(),
                dueDateString  // Use the found due date string or the default "No return date"
            };
            tableModel.addRow(data);
        }
    }
    private void returnSelectedItem() {
        int selectedRow = itemTable.getSelectedRow();
        if (selectedRow != -1) {
            // Ensure this is the correct index for MaterialID
            String materialID = (String) itemTable.getModel().getValueAt(selectedRow, 0);
            String accountID = account.getAccountId();
    
            boolean isReturned = accountDAO.returnLendedItem(materialID, accountID);
            if (isReturned) {
                populateTable((DefaultTableModel) itemTable.getModel(), account.getCheckedOutItems()); // Refresh the table
                JOptionPane.showMessageDialog(this, "Item successfully returned.");
            } else {
                JOptionPane.showMessageDialog(this, "Item could not be returned.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "No item selected.");
        }
    }
        
    
    
}


