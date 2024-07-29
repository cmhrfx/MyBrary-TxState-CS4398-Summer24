package com.example.models;


public abstract class LendingMaterial {
    private int materialID;
    private String type;
    private String available;
    private String checkedOutDate;
    private String checkedOutBy;

    // Constructor
    public LendingMaterial(int materialID, String type) {
        this.materialID = materialID;
        this.type = type;
        this.available = "Yes";
        this.checkedOutDate = null;
        this.checkedOutBy = null;
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

    // Abstract methods to be implemented by subclasses
    public abstract void checkout(String user, String date);
    public abstract void returnMaterial();
}
