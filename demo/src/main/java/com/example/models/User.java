package com.example;

public class User {
    private String userId;
    private Account account;
    private String name;
    private String age;
    private String address;
    private String password;
    private LibraryCard libraryCard;

    // Constructor
    public User(String userId, Account account, String name, String age, String address, String password, LibraryCard libraryCard) {
        this.userId = userId;
        this.account = account;
        this.name = name;
        this.age = age;
        this.address = address;
        this.password = password;
        this.libraryCard = libraryCard;
    }

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LibraryCard getLibraryCard() {
        return libraryCard;
    }

    public void setLibraryCard(LibraryCard libraryCard) {
        this.libraryCard = libraryCard;
    }
}
