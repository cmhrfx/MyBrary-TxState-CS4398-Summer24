package com.example;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;

import javax.swing.*;

import org.bson.Document;

public class App {
    private static LibraryDatabaseConnection dbConnection;
    private static UserDAO userDAO;

    public static void main(String[] args) {
        // Replace with your actual connection string and database name
        String connectionString = "mongodb+srv://chris:bobcats24@cluster0.qxcbrdu.mongodb.net/";
        String databaseName = "Library";

        dbConnection = new LibraryDatabaseConnection(connectionString, databaseName);
        userDAO = new UserDAOImpl(dbConnection);

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

        // Launch the GUI
        SwingUtilities.invokeLater(() -> {
            LibraryView libraryView = new LibraryView(userDAO);
            libraryView.createAndShowGUI();
        });
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
