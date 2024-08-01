package com.example.view;

import com.example.dao.AccountDAO;
import com.example.dao.LendingMaterialDAO;
import com.example.models.Account;
import com.example.models.User;
import com.example.models.LendingMaterial;
import com.example.models.LibraryCard;
import org.bson.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class MyAccountView extends JFrame {
    private Account account;
    private AccountDAO accountDAO;
    private LendingMaterialDAO lendingMaterialDAO;
    private JTable itemTable; // Declare the JTable at the class level
    private User user;

    public MyAccountView(Account account, AccountDAO accountDAO, LendingMaterialDAO lendingMaterialDAO) {
        this.account = account;
        this.accountDAO = accountDAO;
        this.lendingMaterialDAO = lendingMaterialDAO;
        this.user = User.getInstance();

        setTitle("My Account");
        setSize(1200, 750); // Adjusted size for better layout
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // import library card
        System.out.println("MyAccountView:: accountId: " + account.getAccountId());
        LibraryCard libraryCard = accountDAO.getLibraryCard(account.getAccountId());

        // Panel to hold all user info and library card info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        // Display user info and balance
        JPanel userInfoPanel = new JPanel(new GridLayout(0, 1));
        userInfoPanel.add(new JLabel("Name: " + user.getName()));
        userInfoPanel.add(new JLabel("Address: " + user.getAddress()));
        userInfoPanel.add(new JLabel("Balance: $" + account.getBalance()));
        infoPanel.add(userInfoPanel);

        // Display library card information
        JPanel libraryCardPanel = new JPanel(new GridLayout(0, 1));
        libraryCardPanel.add(new JLabel("Library Card Number: " + libraryCard.getLibraryCardNumber()));
        libraryCardPanel.add(new JLabel("User ID: " + user.getUserId()));
        libraryCardPanel.add(new JLabel("Phone Number: " + libraryCard.getPhoneNumber()));
        infoPanel.add(libraryCardPanel);

        add(infoPanel, BorderLayout.NORTH);

        // Initialize itemTable
        String[] columnNames = {"MaterialID", "Title", "Author", "Type", "Due Date", "Actions"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only the "Actions" column is editable
            }
        };
        itemTable = new JTable(tableModel);
        itemTable.setRowHeight(40); // Set the row height to 40 pixels
        itemTable.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        itemTable.getColumn("Actions").setCellEditor(new ButtonEditor(new JCheckBox(), this));
        JScrollPane scrollPane = new JScrollPane(itemTable);
        add(scrollPane, BorderLayout.CENTER);

        // Populate the table
        populateTable(tableModel, account.getCheckedOutItems());

        setVisible(true);
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
                dueDateString,
                "Return/Renew" // Placeholder for buttons
            };
            tableModel.addRow(data);
        }
    }

    public void returnSelectedItem(int selectedRow) {
        if (selectedRow != -1) {
            // Ensure this is the correct index for MaterialID
            String materialID = (String) itemTable.getModel().getValueAt(selectedRow, 0);
            String accountID = account.getAccountId();
    
            boolean isReturned = accountDAO.returnLendedItem(materialID, accountID);
            if (isReturned) {
                // Update the Account object by removing the item
                LendingMaterial item = account.getCheckedOutItemById(materialID);
                lendingMaterialDAO.incrementLendingMaterial(item);

                if (item != null) {
                    account.removeCheckedOutItem(item);
                }
    
                // Refresh the table with updated checked-out items list
                populateTable((DefaultTableModel) itemTable.getModel(), account.getCheckedOutItems());
                JOptionPane.showMessageDialog(this, "Item successfully returned.");
            } else {
                JOptionPane.showMessageDialog(this, "Item could not be returned.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "No item selected.");
        }
    }

    public void renewSelectedItem(int selectedRow) {
        if (selectedRow != -1) {
            String materialID = (String) itemTable.getModel().getValueAt(selectedRow, 0);
            LendingMaterial item = account.getCheckedOutItemById(materialID);
    
            if (accountDAO.reservationExists(item)) {
                JOptionPane.showMessageDialog(this, "Item cannot be renewed due to an existing reservation.");
                return;
            }
    
            LocalDate newReturnDate = accountDAO.getReturnDate(item);
            accountDAO.updateLendedItemReturnDate(materialID, account.getAccountId(), newReturnDate);
            populateTable((DefaultTableModel) itemTable.getModel(), account.getCheckedOutItems()); // Refresh the table
            JOptionPane.showMessageDialog(this, "Item successfully renewed.");
        } else {
            JOptionPane.showMessageDialog(this, "No item selected.");
        }
    }
    

    class ButtonRenderer extends JPanel implements TableCellRenderer {
        private final JButton returnButton = new JButton("Return");
        private final JButton renewButton = new JButton("Renew");

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER));
            returnButton.setPreferredSize(new Dimension(80, 30));
            renewButton.setPreferredSize(new Dimension(80, 30));
            returnButton.setFont(new Font("Arial", Font.PLAIN, 10)); // Set font size
            renewButton.setFont(new Font("Arial", Font.PLAIN, 10)); // Set font size
            add(returnButton);
            add(renewButton);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private final JButton returnButton = new JButton("Return");
        private final JButton renewButton = new JButton("Renew");
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        private final MyAccountView parent;
        private int selectedRow;

        public ButtonEditor(JCheckBox checkBox, MyAccountView parent) {
            super(checkBox);
            this.parent = parent;
            panel.add(returnButton);
            panel.add(renewButton);

            returnButton.addActionListener(e -> returnButtonClicked());
            renewButton.addActionListener(e -> renewButtonClicked());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            selectedRow = row;
            return panel;
        }

        private void returnButtonClicked() {
            parent.returnSelectedItem(selectedRow);
            fireEditingStopped();
        }

        private void renewButtonClicked() {
            parent.renewSelectedItem(selectedRow);
            fireEditingStopped();
        }
    }
}
