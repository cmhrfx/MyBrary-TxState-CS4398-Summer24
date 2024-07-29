package library.view;

import library.model.LibraryModel;
import library.model.User;
import library.model.Item;
import library.model.Book;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CheckoutView extends JFrame {
    private JTable cartTable;
    private DefaultTableModel tableModel;
    private List<Item> cart;
    private LibraryModel model;
    private User user;

    public CheckoutView(LibraryModel model, User user, List<Item> cart) {
        this.model = model;
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
        JButton confirmButton = new JButton("Confirm Checkout");
        JButton removeButton = new JButton("Remove Selected Book");
        buttonPanel.add(confirmButton);
        buttonPanel.add(removeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cart.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No items in cart to checkout.");
                } else {
                    cart.clear(); // Or handle the checkout logic as required
                    JOptionPane.showMessageDialog(null, "Checkout successful!");
                    dispose();
                }
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = cartTable.getSelectedRow();
                if (selectedRow >= 0) {
                    cart.remove(selectedRow);
                    tableModel.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(null, "Book removed from cart.");
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a book to remove.");
                }
            }
        });

        populateTable(cart);
    }

    private void populateTable(List<Item> items) {
        tableModel.setRowCount(0); // Clear existing data
        for (Item item : items) {
            if (item instanceof Book) {
                Book book = (Book) item;
                String[] data = {book.getTitle(), book.getAuthor()};
                tableModel.addRow(data);
            }
        }
    }
}
