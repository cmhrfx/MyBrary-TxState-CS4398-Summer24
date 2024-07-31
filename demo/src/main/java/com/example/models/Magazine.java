package com.example.models;

import org.bson.Document;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Magazine extends LendingMaterial {
    private String genre;
    private String frequency;
    private int issueNumber;

    // Default constructor
    public Magazine() {
        super();
    }

    // Parameterized constructor
    @JsonCreator
    public Magazine(@JsonProperty("MaterialID") String materialID,
                    @JsonProperty("Title") String title,
                    @JsonProperty("Author") String author,
                    @JsonProperty("Type") String type,
                    @JsonProperty("Available") boolean available,
                    @JsonProperty("CheckedOutDate") String checkedOutDate,
                    @JsonProperty("CheckedOutBy") String checkedOutBy,
                    @JsonProperty("CopiesAvailable") int copiesAvailable,
                    @JsonProperty("Genre") String genre,
                    @JsonProperty("Frequency") String frequency,
                    @JsonProperty("IssueNumber") int issueNumber) {
        super(materialID, title, author, type, available, checkedOutDate, checkedOutBy, copiesAvailable);
        this.genre = genre;
        this.frequency = frequency;
        this.issueNumber = issueNumber;
    }

    // Getters and setters
    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public int getIssueNumber() {
        return issueNumber;
    }

    public void setIssueNumber(int issueNumber) {
        this.issueNumber = issueNumber;
    }

    @Override
    public void checkout(String user, String date) {
        if (isAvailable()) {
            setAvailable(false);
            setCheckedOutBy(user);
            setCheckedOutDate(date);
            System.out.println("The magazine '" + getTitle() + "' by " + getAuthor() + " has been checked out by " + user + " on " + date + ".");
        } else {
            System.out.println("The magazine '" + getTitle() + "' is already checked out.");
        }
    }

    @Override
    public void returnMaterial() {
        if (!isAvailable()) {
            setAvailable(true);
            setCheckedOutBy(null);
            setCheckedOutDate(null);
            System.out.println("The magazine '" + getTitle() + "' by " + getAuthor() + " has been returned.");
        } else {
            System.out.println("The magazine '" + getTitle() + "' is not checked out.");
        }
    }

    // Convert to MongoDB Document
    @Override
    public Document toDocument() {
        return super.toDocument();
    }

    // Convert from MongoDB Document
    public static Magazine fromDocument(Document doc) {
        return new Magazine(
                doc.getString("MaterialID"),
                doc.getString("Title"),
                doc.getString("Author"),
                doc.getString("Type"),
                doc.getBoolean("Available"),
                doc.getString("CheckedOutDate"),
                doc.getString("CheckedOutBy"),
                doc.getInteger("CopiesAvailable"),
                doc.getString("Genre"),
                doc.getString("Frequency"),
                doc.getInteger("IssueNumber")
        );
    }
}
