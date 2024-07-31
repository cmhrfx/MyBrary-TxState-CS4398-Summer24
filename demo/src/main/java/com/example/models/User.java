package com.example.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private String userId;
    private String accountId;
    private String name;
    private int age;
    private String address;
    private String password;
    private String libraryCardNumber;
    private String type;

    // Default constructor
    public User() {
    }

    // Parameterized constructor
    @JsonCreator
    public User(@JsonProperty("UserID") String userId,
                @JsonProperty("AccountID") String accountId,
                @JsonProperty("Name") String name,
                @JsonProperty("Age") int age,
                @JsonProperty("Address") String address,
                @JsonProperty("Password") String password,
                @JsonProperty("LibraryCardNumber") String libraryCardNumber,
                @JsonProperty("Type") String type) {
        this.userId = userId;
        this.accountId = accountId;
        this.name = name;
        this.age = age;
        this.address = address;
        this.password = password;
        this.libraryCardNumber = libraryCardNumber;
        this.type = type;
    }

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
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

    public String getLibraryCard() {
        return libraryCardNumber;
    }

    public void setLibraryCard(String libraryCardNumber) {
        this.libraryCardNumber = libraryCardNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
}
