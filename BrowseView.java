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
import java.util.ArrayList;

public class BrowseView extends JFrame {
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private List<Item> cart;
    private LibraryModel model;
    private User user;

    public BrowseView(LibraryModel model, User user) {
        this.model = model;
        this.user = user;
        this.cart = new ArrayList<>();

        setTitle("Browse Books");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        System.out.println("User " + user.getName() + " has logged in.");

        JLabel userInfoLabel = new JLabel("Welcome, " + user.getName());
        add(userInfoLabel, BorderLayout.NORTH);

        String[] columnNames = {"Title", "Author"};
        tableModel = new DefaultTableModel(columnNames, 0);
        bookTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        JButton addButton = new JButton("Add to Cart");
        JButton checkoutButton = new JButton("Checkout");
        panel.add(addButton);
        panel.add(checkoutButton);
        add(panel, BorderLayout.SOUTH);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = bookTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String title = (String) tableModel.getValueAt(selectedRow, 0);
                    String author = (String) tableModel.getValueAt(selectedRow, 1);
                    for (Item item : model.getItems()) {
                        if (item instanceof Book) {
                            Book book = (Book) item;
                            if (book.getTitle().equals(title) && book.getAuthor().equals(author)) {
                                cart.add(book);
                                JOptionPane.showMessageDialog(null, "Book added to cart: " + title);
                                break;
                            }
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a book to add to cart.");
                }
            }
        });

        checkoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cart.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Cart is empty. Add books to cart before checking out.");
                } else {
                    CheckoutView checkoutView = new CheckoutView(model, user, cart);
                    checkoutView.setVisible(true);
                    dispose();
                }
            }
        });

        populateTable(model.getItems());

        setVisible(true);
    }

    private void populateTable(List<Item> items) {
        for (Item item : items) {
            if (item instanceof Book) {
                Book book = (Book) item;
                String[] data = {book.getTitle(), book.getAuthor()};
                tableModel.addRow(data);
            }
        }
    }
}
