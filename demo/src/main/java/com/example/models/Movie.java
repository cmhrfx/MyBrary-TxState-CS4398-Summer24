package com.example.models;

import org.bson.Document;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Movie extends LendingMaterial {
    private String subType;
    private double value; 

    // Default constructor
    public Movie() {
        super();
    }

    // Parameterized constructor
    @JsonCreator
    public Movie(@JsonProperty("MaterialID") String materialID,
                 @JsonProperty("Title") String title,
                 @JsonProperty("Author") String author,
                 @JsonProperty("Type") String type,
                 @JsonProperty("Test") String test,
                 @JsonProperty("CopiesAvailable") int copiesAvailable,
                 @JsonProperty("Value") double value) { 
        super(materialID, title, author, type, test, copiesAvailable);
        this.subType = "Movie";
        this.value = value; 
    }   

    // Getters and setters
    @Override
    public double getValue() {
        return this.value;
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
            System.out.println("The movie '" + getTitle() + "' by " + getAuthor() + " has been checked out by " + user + " on " + date + ".");
        } else {
            System.out.println("The movie '" + getTitle() + "' is already checked out.");
        }
    }

    @Override
    public void returnMaterial() {
        if (!isAvailable()) {
            incrementCopies();
            System.out.println("The movie '" + getTitle() + "' by " + getAuthor() + " has been returned.");
        } else {
            System.out.println("The movie '" + getTitle() + "' is not checked out.");
        }
    }

    // Convert to MongoDB Document
    @Override
    public Document toDocument() {
        return super.toDocument()
                .append("Value", value); 
    }

    // Convert from MongoDB Document
    public static Movie fromDocument(Document doc) {
        return new Movie(
                doc.getString("MaterialID"),
                doc.getString("Title"),
                doc.getString("Author"),
                doc.getString("Type"),
                doc.getString("Test"),
                doc.getInteger("CopiesAvailable"),
                doc.getDouble("Value")
        );
    }
}
