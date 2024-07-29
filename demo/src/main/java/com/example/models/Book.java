package com.example.models;

public class Book extends LendingMaterial {
    private String title;
    private String author;
    private String genre;
    private String height;
    private String publisher;

    // Constructor
    public Book(int materialID, String type, String title, String author, String genre, String height, String publisher) {
        super(materialID, type);
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.height = height;
        this.publisher = publisher;
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

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    // Implementing the abstract methods
    @Override
    public void checkout(String user, String date) {
        if (getAvailable().equals("Yes")) {
            setAvailable("No");
            setCheckedOutBy(user);
            setCheckedOutDate(date);
            System.out.println("The book '" + getTitle() + "' by " + getAuthor() + " has been checked out by " + user + " on " + date + ".");
        } else {
            System.out.println("The book '" + getTitle() + "' is already checked out.");
        }
    }

    @Override
    public void returnMaterial() {
        if (getAvailable().equals("No")) {
            setAvailable("Yes");
            setCheckedOutBy(null);
            setCheckedOutDate(null);
            System.out.println("The book '" + getTitle() + "' by " + getAuthor() + " has been returned.");
        } else {
            System.out.println("The book '" + getTitle() + "' is not checked out.");
        }
    }
}
