package com.example.models;

import org.bson.Document;

public class LibraryCard {
    private String libraryCardNumber;
    private String userID;
    private String phoneNumber;

    // Constructor
    public LibraryCard(String libraryCardNumber, String userID, String phoneNumber) {
        this.libraryCardNumber = libraryCardNumber;
        this.userID = userID;
        this.phoneNumber = phoneNumber;
    }

    // Getters and setters
    public String getLibraryCardNumber() {
        return libraryCardNumber;
    }

    public void setLibraryCardNumber(String libraryCardNumber) {
        this.libraryCardNumber = libraryCardNumber;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // Method to create a LibraryCard object from a MongoDB Document
    public static LibraryCard fromDocument(Document doc) {
        String libraryCardNumber = doc.getString("LibraryCardNumber");
        String userID = doc.getString("AccountID");
        String phoneNumber = doc.getString("Phone Number");

        return new LibraryCard(libraryCardNumber, userID, phoneNumber);
    }
}
