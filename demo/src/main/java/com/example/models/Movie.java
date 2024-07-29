package com.example.models;

public class Movie extends LendingMaterial {
    private String title;
    private String author;

    // Constructor
    public Movie(int materialID, String type, String title, String author) {
        super(materialID, type);
        this.title = title;
        this.author = author;
    }

    // Getters and setters
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

    // Implementing the abstract methods
    @Override
    public void checkout(String user, String date) {
        if (getAvailable().equals("Yes")) {
            setAvailable("No");
            setCheckedOutBy(user);
            setCheckedOutDate(date);
            System.out.println("The movie '" + getTitle() + "' directed by " + getAuthor() + " has been checked out by " + user + " on " + date + ".");
        } else {
            System.out.println("The movie '" + getTitle() + "' is already checked out.");
        }
    }

    @Override
    public void returnMaterial() {
        if (getAvailable().equals("No")) {
            setAvailable("Yes");
            setCheckedOutBy(null);
            setCheckedOutDate(null);
            System.out.println("The movie '" + getTitle() + "' directed by " + getAuthor() + " has been returned.");
        } else {
            System.out.println("The movie '" + getTitle() + "' is not checked out.");
        }
    }
}
