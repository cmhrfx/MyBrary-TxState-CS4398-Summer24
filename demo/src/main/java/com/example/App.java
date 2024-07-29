package com.example;

import com.mongodb.client.MongoDatabase;

import javax.swing.*;

public class App {
    private static LibraryDatabaseConnection dbConnection;
    private static UserDAO userDAO;

    public static void main(String[] args) {
        // Replace with your actual connection string and database name
        String connectionString = "mongodb+srv://chris:bobcats24@cluster0.qxcbrdu.mongodb.net/";
        String databaseName = "Library";

        dbConnection = new LibraryDatabaseConnection(connectionString, databaseName);
        userDAO = new UserDAOImpl(dbConnection);

        // Launch the GUI
        SwingUtilities.invokeLater(() -> {
            LibraryView libraryView = new LibraryView(userDAO);
            libraryView.createAndShowGUI();
        });
    }
}
