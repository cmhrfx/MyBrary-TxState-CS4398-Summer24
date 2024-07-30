package com.example.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "Type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Book.class, name = "Book"),
    @JsonSubTypes.Type(value = Movie.class, name = "Movie")
})
public abstract class LendingMaterial {
    private String materialID;
    private String type;
    private boolean available;
    private String checkedOutDate;
    private String checkedOutBy;
    private int copiesAvailable;

    // Default constructor
    public LendingMaterial() {
    }

    // Parameterized constructor
    @JsonCreator
    public LendingMaterial(@JsonProperty("MaterialID") String materialID,
                           @JsonProperty("Type") String type,
                           @JsonProperty("Available") boolean available,
                           @JsonProperty("CheckedOutDate") String checkedOutDate,
                           @JsonProperty("CheckedOutBy") String checkedOutBy,
                           @JsonProperty("CopiesAvailable") int copiesAvailable) {
        this.materialID = materialID;
        this.type = type;
        this.available = available;
        this.checkedOutDate = checkedOutDate;
        this.checkedOutBy = checkedOutBy;
        this.copiesAvailable = copiesAvailable;
    }

    // Getters and setters
    public String getMaterialID() {
        return materialID;
    }

    public void setMaterialID(String materialID) {
        this.materialID = materialID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean getAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getCheckedOutDate() {
        return checkedOutDate;
    }

    public void setCheckedOutDate(String checkedOutDate) {
        this.checkedOutDate = checkedOutDate;
    }

    public String getCheckedOutBy() {
        return checkedOutBy;
    }

    public void setCheckedOutBy(String checkedOutBy) {
        this.checkedOutBy = checkedOutBy;
    }

    public int getCopiesAvailable() {
        return this.copiesAvailable;
    }

    public void setCopiesAvailable(int copies) {
        this.copiesAvailable = copies;
    }

    public double calculateOverdueFine(int overdueDays) {
        double fine = overdueDays * 0.10;
        return Math.min(fine, getMaxFine());
    }

    private double getMaxFine() {
        // Return the maximum fine, which could be the value of the book or some predefined value
        return 20.0; // Example value
    }

    public boolean isAvailable() {
        return (copiesAvailable > 0);
    }

    // Abstract methods to be implemented by subclasses
    public abstract void checkout(String user, String date);
    public abstract void returnMaterial();
}
