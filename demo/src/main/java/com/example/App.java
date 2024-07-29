package com.example;

import com.mongodb.client.MongoDatabase;

public class App {
    public static void main(String[] args) {
        // Replace with your actual connection string and database name
        String connectionString = "mongodb+srv://chris:bobcats24@cluster0.qxcbrdu.mongodb.net/";
        String databaseName = "Cluster0";

        LibraryDatabaseConnection dbConnection = new LibraryDatabaseConnection(connectionString, databaseName);
        MongoDatabase database = dbConnection.getDatabase();

        // Perform database operations
        System.out.println("Connected to the database: " + database.getName());

        // Remember to close the connection when done
        dbConnection.close();
    }
}
