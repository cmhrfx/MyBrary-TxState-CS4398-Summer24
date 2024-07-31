package com.example.models;

import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Account {
    @BsonId
    private String accountId;
    private String userId;
    private String type;
    @BsonProperty("checkedOutItems")
    private List<LendingMaterial> checkedOutItems;
    private double balance;

    // Constructor
    public Account(String accountId, String userId, String type, double balance) {
        this.accountId = accountId;
        this.userId = userId;
        this.type = type;
        this.checkedOutItems = new ArrayList<>();
        this.balance = balance;
    }

    // Getters and setters
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<LendingMaterial> getCheckedOutItems() {
        return checkedOutItems;
    }

    public void setCheckedOutItems(List<LendingMaterial> checkedOutItems) {
        this.checkedOutItems = checkedOutItems;
    }

    public void addCheckedOutItem(LendingMaterial item) {
        this.checkedOutItems.add(item);
    }

    public void removeCheckedOutItem(LendingMaterial item) {
        this.checkedOutItems.remove(item);
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    // Serialize the Account object to a MongoDB Document
    public Document toDocument() {
        List<Document> checkedOutItemsDocs = new ArrayList<>();
        for (LendingMaterial item : checkedOutItems) {
            checkedOutItemsDocs.add(item.toDocument());
        }

        Document doc = new Document("AccountID", accountId)
                .append("UserID", userId)
                .append("Type", type)
                .append("CheckedOutItems", checkedOutItemsDocs)
                .append("Balance", balance);
        return doc;
    }

    // Deserialize a MongoDB Document to an Account object
    public static Account fromDocument(Document doc) {
        Account account = new Account(doc.getString("AccountID"),
                                      doc.getString("UserID"),
                                      doc.getString("Type"),
                                      doc.getDouble("Balance").doubleValue());
    
        List<Document> checkedOutItemsDocs = (List<Document>) doc.get("CheckedOutItems");
        List<LendingMaterial> checkedOutItems = new ArrayList<>();
        for (Document itemDoc : checkedOutItemsDocs) {
            LendingMaterial item = null;
            String type = itemDoc.getString("Type");
            
            if (type != null) {
                switch (type) {
                    case "Book":
                        item = Book.fromDocument(itemDoc);
                        break;
                    case "Movie":
                        item = Movie.fromDocument(itemDoc);
                        break;
                    // add cases for other types if needed
                    default:
                        System.err.println("Unknown type: " + type);
                        break;
                }
            } else {
                System.err.println("Type is null in document: " + itemDoc);
            }
            
            if (item != null) {
                checkedOutItems.add(item);
            }
        }
        account.setCheckedOutItems(checkedOutItems);
        return account;
    }
}
