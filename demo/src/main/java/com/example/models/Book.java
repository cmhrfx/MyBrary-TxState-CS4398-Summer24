package com.example.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Book extends LendingMaterial {
    private String title;
    private String author;
    private String genre;
    private int height;
    private String publisher;

    // Default constructor
    public Book() {
        super();
    }

    // Parameterized constructor
    @JsonCreator
    public Book(@JsonProperty("MaterialID") String materialID,
                @JsonProperty("Type") String type,
                @JsonProperty("Title") String title,
                @JsonProperty("Author") String author,
                @JsonProperty("Genre") String genre,
                @JsonProperty("Height") int height,
                @JsonProperty("Publisher") String publisher,
                @JsonProperty("Available") boolean available,
                @JsonProperty("CheckedOutDate") String checkedOutDate,
                @JsonProperty("CheckedOutBy") String checkedOutBy,
                @JsonProperty("CopiesAvailable") int copiesAvailable) {
        super(materialID, type, available, checkedOutDate, checkedOutBy, copiesAvailable);
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.height = height;
        this.publisher = publisher;
    }

    // Getters and Setters
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

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    @Override
    public void checkout(String user, String date) {
        if (isAvailable()) {
            setAvailable(false);
            setCheckedOutBy(user);
            setCheckedOutDate(date);
            System.out.println("The book '" + getTitle() + "' by " + getAuthor() + " has been checked out by " + user + " on " + date + ".");
        } else {
            System.out.println("The book '" + getTitle() + "' is already checked out.");
        }
    }

    @Override
    public void returnMaterial() {
        if (!isAvailable()) {
            setAvailable(true);
            setCheckedOutBy(null);
            setCheckedOutDate(null);
            System.out.println("The book '" + getTitle() + "' by " + getAuthor() + " has been returned.");
        } else {
            System.out.println("The book '" + getTitle() + "' is not checked out.");
        }
    }
}
