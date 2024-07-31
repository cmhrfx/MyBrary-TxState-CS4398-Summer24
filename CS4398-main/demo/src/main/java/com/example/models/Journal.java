package com.example.models;

import org.bson.Document;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Journal extends LendingMaterial {
    private String subType;

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
                   @JsonProperty("Available") boolean available,
                   @JsonProperty("CheckedOutDate") String checkedOutDate,
                   @JsonProperty("CheckedOutBy") String checkedOutBy,
                   @JsonProperty("CopiesAvailable") int copiesAvailable) {
        super(materialID, title, author, type, available, checkedOutDate, checkedOutBy, copiesAvailable);

        this.subType = "Journal";
    }

    @Override
    public void checkout(String user, String date) {
        if (isAvailable()) {
            setAvailable(false);
            setCheckedOutBy(user);
            setCheckedOutDate(date);
            System.out.println("The journal '" + getTitle() + "' by " + getAuthor() + " has been checked out by " + user + " on " + date + ".");
        } else {
            System.out.println("The journal '" + getTitle() + "' is already checked out.");
        }
    }

    @Override
    public void returnMaterial() {
        if (!isAvailable()) {
            setAvailable(true);
            setCheckedOutBy(null);
            setCheckedOutDate(null);
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
                doc.getBoolean("Available"),
                doc.getString("CheckedOutDate"),
                doc.getString("CheckedOutBy"),
                doc.getInteger("CopiesAvailable")
        );
    }
}
