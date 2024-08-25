package com.example;

import com.example.controller.LoginController;
import com.example.controller.BrowseController;
import com.example.controller.CheckoutController;
import com.example.dao.LendingMaterialDAO;
import com.example.dao.LendingMaterialDAOImpl;
import com.example.dao.UserDAO;
import com.example.dao.UserDAOImpl;
import com.example.dao.AccountDAO;
import com.example.dao.AccountDAOImpl;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import com.example.models.Account;
import com.example.models.Book;
import com.example.models.Cart;
import com.example.models.LendingMaterial;
import com.example.models.Movie;
import com.example.models.User;
import com.example.view.BrowseView;
import com.example.view.LoginView;
import com.example.view.CheckoutView;
import com.example.view.MyAccountView;

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.bson.Document;

public class App {
    private static LibraryDatabaseConnection dbConnection;
    private static UserDAO userDAO;
    private static LendingMaterialDAO lendingMaterialDAO;
    private static AccountDAO accountDAO;
    private static Account account;
    private static User user;
    private static Cart cart;

    public static void main(String[] args) {
        // database connection parameters
        String connectionString = "mongodb+srv://chris:******@cluster0.qxcbrdu.mongodb.net/";
        String databaseName = "Library";

        // DAO object instantiation
        dbConnection = new LibraryDatabaseConnection(connectionString, databaseName);
        userDAO = new UserDAOImpl(dbConnection);
        lendingMaterialDAO = new LendingMaterialDAOImpl(dbConnection);
        accountDAO = new AccountDAOImpl(dbConnection);

        // Update OverDue Items
        // updateOverdueItems(accountDAO);
        accountDAO.updateLendedItemFees();

        // Update Account Balances
        // updateAccountBalance(accountDAO);
        accountDAO.updateAccountBalance();

        // MAIN DEBUG
        MongoDatabase database = dbConnection.getDatabase();
        System.out.println("Database found: " + (database != null));
        boolean usersCollectionExists = collectionExists(database, "Users");
        System.out.println("Users collection exists: " + usersCollectionExists);
        Document doc = database.getCollection("Users")
                        .find(Filters.eq("UserID", "0")).first();
        if(doc != null)
        {
            System.out.println("Found test user");
        } else {
            System.out.println("Failed to find test user");
        }
        boolean accountsCollectionExists = collectionExists(database, "Accounts");
        System.out.println("Accounts collection exists: " + accountsCollectionExists);
        boolean lendingMaterialCollectionExists = 
                collectionExists(database, "LendingMaterial");
        System.out.println
                ("LendingMaterial collection exists: " + lendingMaterialCollectionExists);
        boolean libraryCardsCollectionExists = 
                collectionExists(database, "LibraryCards");
        System.out.println("LibraryCards collection exists: " + libraryCardsCollectionExists);

        // END OF DEBUG

        // Initialize the Cart
        cart = Cart.getInstance();
        System.out.println("Cart initialized: " + (cart != null));

        // initialize the user
        user = User.getInstance();
        user.setUserId("-1");
        user.setAccountId("0");
        user.setName("guest");
        user.setAge(18);
        user.setAddress("guest");
        user.setPassword("guest");
        user.setLibraryCard("guest");
        user.setType("guest");

        // Initialize the views
        LoginView loginView = new LoginView();
        BrowseView browseView = new BrowseView(user, cart, lendingMaterialDAO, accountDAO);        
        CheckoutView checkoutView = new CheckoutView(user, cart, accountDAO);
        // MyAccountView accountView = new MyAccountView(account, accountDAO, lendingMaterialDAO);

        // Initialize the login controller
        new LoginController(userDAO, lendingMaterialDAO, accountDAO, loginView, browseView, cart, user);
        new BrowseController(loginView, browseView, checkoutView, user, lendingMaterialDAO, accountDAO, cart);
        new CheckoutController(lendingMaterialDAO, browseView, checkoutView, cart, user, accountDAO);

        // Make the login view visible
        loginView.setVisible(true);
    }

    private static boolean collectionExists(MongoDatabase database, String collectionName) {
        MongoIterable<String> collections = database.listCollectionNames();
        for (String name : collections) {
            if (name.equalsIgnoreCase(collectionName)) {
                return true;
            }
        }
        return false;
    }

    public static void updateOverdueItems(AccountDAO accountDAO) {
        // Get today's date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();

        // Find all lended items
        List<Document> lendedItems = accountDAO.getAllLendedItems();

        for (Document item : lendedItems) {
            String returnDateStr = item.getString("ReturnDate");

            if (returnDateStr != null && !returnDateStr.isEmpty()) {
                LocalDate returnDate = LocalDate.parse(returnDateStr, formatter);
                long daysOverdue = ChronoUnit.DAYS.between(returnDate, today);

                if (daysOverdue > 0) {
                    // Update the DaysOverdue field
                    accountDAO.updateLendedItemDaysOverdue(item, daysOverdue);
                }
            }
        }
    }

    public static void updateAccountBalance(AccountDAO accountDAO) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();

        List<Document> lendedItems = accountDAO.getAllLendedItems();

        for (Document item : lendedItems) {
            String returnDateStr = item.getString("ReturnDate");
            String lastBalanceUpdateStr = item.getString("LastBalanceUpdate");

            if (returnDateStr != null && !returnDateStr.isEmpty()) {
                LocalDate returnDate = LocalDate.parse(returnDateStr, formatter);
                long daysOverdue = ChronoUnit.DAYS.between(returnDate, today);

                if (daysOverdue > 0) {
                    LocalDate lastBalanceUpdate = lastBalanceUpdateStr == null || lastBalanceUpdateStr.isEmpty()
                            ? returnDate : LocalDate.parse(lastBalanceUpdateStr, formatter);
                    long daysSinceLastUpdate = ChronoUnit.DAYS.between(lastBalanceUpdate, today);

                    if (daysSinceLastUpdate > 0) {
                        String accountId = item.getString("AccountID");
                        String materialId = item.getString("MaterialID");
                        LendingMaterial lendingMaterial = lendingMaterialDAO.getLendingMaterialById(materialId);

                        if (lendingMaterial != null) {
                            if (lendingMaterial instanceof Book) {
                                updateBookBalance((Book) lendingMaterial, daysSinceLastUpdate, accountId, accountDAO, item, today, daysOverdue);
                            } else if (lendingMaterial instanceof Movie) {
                                updateMovieBalance((Movie) lendingMaterial, daysSinceLastUpdate, accountId, accountDAO, item, today, daysOverdue);
                            }
                        }
                    }
                }
            }
        }
    }

    private static void updateBookBalance(Book book, long daysSinceLastUpdate, String accountId, AccountDAO accountDAO, Document item, LocalDate today, long daysOverdue) {
        double overdueFee = daysSinceLastUpdate * 0.10;
        double overallOverdueFee = overdueFee + daysOverdue * 0.10;
        double maxFee = book.getValue();
        if (overallOverdueFee > maxFee) {
            accountDAO.setAccountBalance(accountId, maxFee);
        } else {
            accountDAO.incrementAccountBalance(accountId, overdueFee);
        }
        accountDAO.updateLendedItemLastBalanceUpdate(item, today);
    }

    private static void updateMovieBalance(Movie movie, long daysSinceLastUpdate, String accountId, AccountDAO accountDAO, Document item, LocalDate today, long daysOverdue) {
        double overdueFee = daysSinceLastUpdate * 0.10;
        double overallOverdueFee = overdueFee + daysOverdue * 0.10;
        double maxFee = movie.getValue();
        if (overallOverdueFee > maxFee) {
            accountDAO.setAccountBalance(accountId, overallOverdueFee);
        } else {
            accountDAO.incrementAccountBalance(accountId, overdueFee);
        }
        accountDAO.updateLendedItemLastBalanceUpdate(item, today);
    }
}