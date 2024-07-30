package com.example.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Movie extends LendingMaterial {
    private String title;
    private String author;

    // Default constructor
    public Movie() {
        super();
    }

    // Parameterized constructor
    @JsonCreator
    public Movie(@JsonProperty("MaterialID") String materialID,
                 @JsonProperty("Type") String type,
                 @JsonProperty("Title") String title,
                 @JsonProperty("Author") String author,
                 @JsonProperty("Available") boolean available,
                 @JsonProperty("CheckedOutDate") String checkedOutDate,
                 @JsonProperty("CheckedOutBy") String checkedOutBy,
                 @JsonProperty("CopiesAvailable") int copiesAvailable) {
        super(materialID, type, available, checkedOutDate, checkedOutBy, copiesAvailable);
        this.title = title;
        this.author = author;
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    // Implementing the abstract methods
    @Override
    public void checkout(String user, String date) {
        if (isAvailable()) {
            setAvailable(false);
            setCheckedOutBy(user);
            setCheckedOutDate(date);
            System.out.println("The movie '" + getTitle() + "' by " + getAuthor() + " has been checked out by " + user + " on " + date + ".");
        } else {
            System.out.println("The movie '" + getTitle() + "' is already checked out.");
        }
    }
    
    @Override
    public void returnMaterial() {
        if (!isAvailable()) {
            setAvailable(true);
            setCheckedOutBy(null);
            setCheckedOutDate(null);
            System.out.println("The movie '" + getTitle() + "' by " + getAuthor() + " has been returned.");
        } else {
            System.out.println("The movie '" + getTitle() + "' is not checked out.");
        }
    }
}
