package com.example.models;

import org.bson.Document;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Journal extends LendingMaterial {
    private String subType;
    private static final double DEFAULT_VALUE = 5.0;

    // Default constructor
    public Journal() {
        super();
    }

    // Parameterized constructor
    @JsonCreator
    public Journal(@JsonProperty("MaterialID") String materialID,
                   @JsonProperty("Title") String title,
                   @JsonProperty("Author") String author,
                   @JsonProperty("Type") String type,
                   @JsonProperty("Test") String test,
                   @JsonProperty("CopiesAvailable") int copiesAvailable) {
        super(materialID, title, author, type, test, copiesAvailable);

        this.subType = "Journal";
    }

    @Override
    public double getValue() {
        return DEFAULT_VALUE;
    }

    @Override
    public void checkout(String user, String date) {
        if (isAvailable()) {
            decrementCopies();
            System.out.println("The journal '" + getTitle() + "' by " + getAuthor() + " has been checked out by " + user + " on " + date + ".");
        } else {
            System.out.println("The journal '" + getTitle() + "' is already checked out.");
        }
    }

    @Override
    public void returnMaterial() {
        if (!isAvailable()) {
            incrementCopies();
            System.out.println("The journal '" + getTitle() + "' by " + getAuthor() + " has been returned.");
        } else {
            System.out.println("The journal '" + getTitle() + "' is not checked out.");
        }
    }

    @Override
    public String getSubType() {
        return subType;
    }

    // Convert to MongoDB Document
    @Override
    public Document toDocument() {
        return super.toDocument();
    }

    // Convert from MongoDB Document
    public static Journal fromDocument(Document doc) {
        return new Journal(
                doc.getString("MaterialID"),
                doc.getString("Title"),
                doc.getString("Author"),
                doc.getString("Type"),
                doc.getString("Test"),
                doc.getInteger("CopiesAvailable")
        );
    }
}
