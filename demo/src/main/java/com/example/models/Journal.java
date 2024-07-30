package com.example.models;

import org.bson.Document;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Journal extends LendingMaterial {
    private String volume;
    private String issue;
    private int pages;

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
                   @JsonProperty("CopiesAvailable") int copiesAvailable,
                   @JsonProperty("Volume") String volume,
                   @JsonProperty("Issue") String issue,
                   @JsonProperty("Pages") int pages) {
        super(materialID, title, author, type, available, checkedOutDate, checkedOutBy, copiesAvailable);
        this.volume = volume;
        this.issue = issue;
        this.pages = pages;
    }

    // Getters and setters
    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
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

    // Convert to MongoDB Document
    @Override
    public Document toDocument() {
        return super.toDocument()
                .append("volume", volume)
                .append("issue", issue)
                .append("pages", pages);
    }

    // Convert from MongoDB Document
    public static Journal fromDocument(Document doc) {
        return new Journal(
                doc.getString("materialID"),
                doc.getString("title"),
                doc.getString("author"),
                doc.getString("type"),
                doc.getBoolean("available"),
                doc.getString("checkedOutDate"),
                doc.getString("checkedOutBy"),
                doc.getInteger("copiesAvailable"),
                doc.getString("volume"),
                doc.getString("issue"),
                doc.getInteger("pages")
        );
    }
}
