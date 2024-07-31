package com.example.models;

import org.bson.Document;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Book extends LendingMaterial {
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
                @JsonProperty("Title") String title,
                @JsonProperty("Author") String author,
                @JsonProperty("Type") String type,
                @JsonProperty("Available") boolean available,
                @JsonProperty("CheckedOutDate") String checkedOutDate,
                @JsonProperty("CheckedOutBy") String checkedOutBy,
                @JsonProperty("CopiesAvailable") int copiesAvailable,
                @JsonProperty("Genre") String genre,
                @JsonProperty("Height") int height,
                @JsonProperty("Publisher") String publisher) {
        super(materialID, title, author, type, available, checkedOutDate, checkedOutBy, copiesAvailable);
        this.genre = genre;
        this.height = height;
        this.publisher = publisher;
    }

    // Getters and setters
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

    // Convert to MongoDB Document
    @Override
    public Document toDocument() {
        return super.toDocument()
                .append("Genre", genre)
                .append("Height", height)
                .append("Publisher", publisher);
    }

    // Convert from MongoDB Document
    public static Book fromDocument(Document doc) {
        return new Book(
                doc.getString("MaterialID"),
                doc.getString("Title"),
                doc.getString("Author"),
                doc.getString("Type"),
                doc.getBoolean("Available"),
                doc.getString("CheckedOutDate"),
                doc.getString("CheckedOutBy"),
                doc.getInteger("CopiesAvailable"),
                doc.getString("Genre"),
                doc.getInteger("Height"),
                doc.getString("Publisher")
        );
    }
}
