package com.example;

import com.example.controller.LoginController;
import com.example.dao.LendingMaterialDAO;
import com.example.dao.LendingMaterialDAOImpl;
import com.example.dao.UserDAO;
import com.example.dao.UserDAOImpl;
import com.example.dao.AccountDAO;
import com.example.dao.AccountDAOImpl;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import com.example.models.Cart;
import com.example.view.LoginView;

import javax.swing.*;

import org.bson.Document;

public class App {
    private static LibraryDatabaseConnection dbConnection;
    private static UserDAO userDAO;
    private static LendingMaterialDAO lendingMaterialDAO;
    private static AccountDAO accountDAO;

    public static void main(String[] args) {
        // Replace with your actual connection string and database name
        String connectionString = "mongodb+srv://chris:bobcats24@cluster0.qxcbrdu.mongodb.net/";
        String databaseName = "Library";

        dbConnection = new LibraryDatabaseConnection(connectionString, databaseName);
        userDAO = new UserDAOImpl(dbConnection);
        lendingMaterialDAO = new LendingMaterialDAOImpl(dbConnection);
        accountDAO = new AccountDAOImpl(dbConnection);

        // MAIN DEBUG
        MongoDatabase database = dbConnection.getDatabase();
        
        // ABLE TO FIND DATABASE?
        System.out.println("Database found: " + (database != null));
        
        // ABLE TO FIND USERS COLLECTION?
        boolean usersCollectionExists = collectionExists(database, "Users");
        System.out.println("Users collection exists: " + usersCollectionExists);

        // TEST USER CASE
        Document doc = database.getCollection("Users").find(Filters.eq("UserID", "0")).first();
        if(doc != null)
        {
            System.out.println("Found test user");
        } else {
            System.out.println("Failed to find test user");
        }

        // ABLE TO FIND ACCOUNTS COLLECTION?
        boolean accountsCollectionExists = collectionExists(database, "Accounts");
        System.out.println("Accounts collection exists: " + accountsCollectionExists);

        // ABLE TO FIND LENDINGMATERIAL COLLECTION?
        boolean lendingMaterialCollectionExists = collectionExists(database, "LendingMaterial");
        System.out.println("LendingMaterial collection exists: " + lendingMaterialCollectionExists);

        // ABLE TO FIND LIBRARYCARDS COLLECTION?
        boolean libraryCardsCollectionExists = collectionExists(database, "LibraryCards");
        System.out.println("LibraryCards collection exists: " + libraryCardsCollectionExists);

        // Initialize the Cart
        Cart cart = new Cart();
        System.out.println("Cart initialized: " + (cart != null));

        // Initialize the login view
        LoginView loginView = new LoginView();

        // Initialize the login controller
        new LoginController(userDAO, lendingMaterialDAO, accountDAO, loginView, cart);

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
}
