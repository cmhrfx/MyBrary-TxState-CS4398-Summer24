package com.example.models;

import org.bson.Document;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Book extends LendingMaterial {
    private String genre;
    private int height;
    private String publisher;
    private String subType;
    private Boolean bestSeller;
    private double value;

    // Default constructor
    public Book() {
        super();
        this.subType = "Book";
    }

    // Parameterized constructor
    @JsonCreator
    public Book(@JsonProperty("MaterialID") String materialID,
                @JsonProperty("Title") String title,
                @JsonProperty("Author") String author,
                @JsonProperty("Type") String type,
                @JsonProperty("Test") String test,
                @JsonProperty("CopiesAvailable") int copiesAvailable,
                @JsonProperty("Genre") String genre,
                @JsonProperty("Height") int height,
                @JsonProperty("Publisher") String publisher,
                @JsonProperty("BestSeller") Boolean bestSeller,
                @JsonProperty("Value") double value) {
        super(materialID, title, author, type, test, copiesAvailable);
        this.subType = "Book";
        this.genre = genre;
        this.height = height;
        this.publisher = publisher;
        this.bestSeller = bestSeller;
        this.value = value;
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

    public Boolean getBestSeller() {
        return bestSeller;
    }

    public void setBestSeller(Boolean bestSeller) {
        this.bestSeller = bestSeller;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String getSubType() {
        return subType;
    }

    @Override
    public void checkout(String user, String date) {
        if (isAvailable()) {
            decrementCopies();
            System.out.println("The book '" + getTitle() + "' by " + getAuthor() + " has been checked out by " + user + " on " + date + ".");
        } else {
            System.out.println("The book '" + getTitle() + "' is already checked out.");
        }
    }

    @Override
    public void returnMaterial() {
        if (!isAvailable()) {
            incrementCopies();
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
                .append("Publisher", publisher)
                .append("BestSeller", bestSeller)
                .append("Value", value);
    }

    // Convert from MongoDB Document
    public static Book fromDocument(Document doc) {
        return new Book(
                doc.getString("MaterialID"),
                doc.getString("Title"),
                doc.getString("Author"),
                doc.getString("Type"),
                doc.getString("Test"),
                doc.getInteger("CopiesAvailable"),
                doc.getString("Genre"),
                doc.getInteger("Height"),
                doc.getString("Publisher"),
                doc.getBoolean("BestSeller"),
                doc.getDouble("Value")
        );
    }
}
