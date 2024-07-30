package com.example.models;

import org.bson.Document;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Movie extends LendingMaterial {
    private String genre;
    private int duration; // duration in minutes

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
                 @JsonProperty("Available") boolean available,
                 @JsonProperty("CheckedOutDate") String checkedOutDate,
                 @JsonProperty("CheckedOutBy") String checkedOutBy,
                 @JsonProperty("CopiesAvailable") int copiesAvailable,
                 @JsonProperty("Genre") String genre,
                 @JsonProperty("Duration") int duration) {
        super(materialID, title, author, type, available, checkedOutDate, checkedOutBy, copiesAvailable);
        this.genre = genre;
        this.duration = duration;
    }

    // Getters and setters
    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

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

    // Convert to MongoDB Document
    @Override
    public Document toDocument() {
        return super.toDocument()
                .append("genre", genre)
                .append("duration", duration);
    }

    // Convert from MongoDB Document
    public static Movie fromDocument(Document doc) {
        return new Movie(
                doc.getString("materialID"),
                doc.getString("title"),
                doc.getString("author"),
                doc.getString("type"),
                doc.getBoolean("available"),
                doc.getString("checkedOutDate"),
                doc.getString("checkedOutBy"),
                doc.getInteger("copiesAvailable"),
                doc.getString("genre"),
                doc.getInteger("duration")
        );
    }
}
