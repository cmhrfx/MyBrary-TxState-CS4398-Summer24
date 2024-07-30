package com.example.models;

public abstract class LendingMaterial {
    private int materialID;
    private String type;
    private String available;
    private String checkedOutDate;
    private String checkedOutBy;
    private int copiesAvailable;

    // Constructor
    public LendingMaterial(int materialID, String type) {
        this.materialID = materialID;
        this.type = type;
        this.available = "Yes";
        this.checkedOutDate = null;
        this.checkedOutBy = null;
        this.copiesAvailable = 1;
    }

    // Getters and setters
    public int getMaterialID() {
        return materialID;
    }

    public void setMaterialID(int materialID) {
        this.materialID = materialID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
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

    // Abstract methods to be implemented by subclasses
    public abstract void checkout(String user, String date);
    public abstract void returnMaterial();
}
