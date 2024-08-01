package com.example.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;



import org.bson.Document;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "Type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Book.class, name = "Book"),
    @JsonSubTypes.Type(value = Movie.class, name = "Movie"),
    @JsonSubTypes.Type(value = Magazine.class, name = "Magazine"),
    @JsonSubTypes.Type(value = Journal.class, name = "Journal")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class LendingMaterial {
    private String materialID;
    private String title;
    private String author;
    private String type;
    private String test;
    private int copiesAvailable;

    // Default constructor
    public LendingMaterial() {
    }

    // Parameterized constructor
    @JsonCreator
    public LendingMaterial(@JsonProperty("MaterialID") String materialID,
                           @JsonProperty("Title") String title,
                           @JsonProperty("Author") String author,
                           @JsonProperty("Type") String type,
                           @JsonProperty("Test") String test,
                           @JsonProperty("CopiesAvailable") int copiesAvailable) {
        this.materialID = materialID;
        this.title = title;
        this.author = author;
        this.type = type;
        this.test = test;
        this.copiesAvailable = copiesAvailable;
    }

    // Getters and setters

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getMaterialID() {
        return materialID;
    }

    public void setMaterialID(String materialID) {
        this.materialID = materialID;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCopiesAvailable() {
        return copiesAvailable;
    }

    public void setCopiesAvailable(int copiesAvailable) {
        this.copiesAvailable = copiesAvailable;
    }

    public double calculateOverdueFine(int overdueDays) {
        double fine = overdueDays * 0.10;
        return Math.min(fine, getMaxFine());
    }

    public boolean isAvailable() {
        return copiesAvailable > 0;
    }

    public void incrementCopies() {
        copiesAvailable++;
    }

    public void decrementCopies() {
        copiesAvailable--;
    }

    private double getMaxFine() {
        // Return the maximum fine, which could be the value of the book or some predefined value
        return 20.0; // Example value
    }

    public Document toDocument() {
        return new Document("MaterialID", materialID)
                .append("Title", title)
                .append("Author", author)
                .append("Type", type)
                .append("Test", test)
                .append("CopiesAvailable", copiesAvailable);
    }

    // Abstract methods to be implemented by subclasses
    public abstract void checkout(String user, String date);
    public abstract void returnMaterial();
    public abstract String getSubType();
    public abstract double getValue();
}
