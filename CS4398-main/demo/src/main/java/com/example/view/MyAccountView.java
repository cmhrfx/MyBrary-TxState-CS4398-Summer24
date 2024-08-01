package com.example.view;

import com.example.dao.AccountDAO;
import com.example.models.Account;
import com.example.models.LendingMaterial;

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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        tableModel.setRowCount(0); // Clear existing data
    
        for (LendingMaterial item : items) {
            // Safely handle potentially null return dates
            String dueDateString = (item.getReturnDate() != null) ? item.getReturnDate().format(formatter) : "No return date";
            String[] data = {
                item.getTitle(),
                item.getAuthor(),
                item.getSubType(),
                dueDateString  // Use the safely formatted due date string or a placeholder
            };
            tableModel.addRow(data);
        }
    }
}