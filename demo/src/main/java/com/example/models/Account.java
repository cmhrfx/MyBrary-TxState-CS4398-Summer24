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
    private float balance;

    // Constructor
    public Account(String accountId, String userId, String type, float balance) {
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

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    // Serialize the Account object to a MongoDB Document
    public Document toDocument() {
        List<Document> checkedOutItemsDocs = new ArrayList<>();
        for (LendingMaterial item : checkedOutItems) {
            checkedOutItemsDocs.add(item.toDocument());
        }

        Document doc = new Document("accountId", accountId)
                .append("userId", userId)
                .append("type", type)
                .append("checkedOutItems", checkedOutItemsDocs)
                .append("balance", balance);
        return doc;
    }

    // Deserialize a MongoDB Document to an Account object
    public static Account fromDocument(Document doc) {
        Account account = new Account(doc.getString("accountId"),
                                      doc.getString("userId"),
                                      doc.getString("type"),
                                      doc.getDouble("balance").floatValue());

        List<Document> checkedOutItemsDocs = (List<Document>) doc.get("checkedOutItems");
        List<LendingMaterial> checkedOutItems = new ArrayList<>();
        for (Document itemDoc : checkedOutItemsDocs) {
            LendingMaterial item;
            if ("Book".equals(itemDoc.getString("type"))) {
                item = Book.fromDocument(itemDoc);
            } else if ("Movie".equals(itemDoc.getString("type"))) {
                item = Movie.fromDocument(itemDoc);
            } else {
                // handle other types or throw an exception
                throw new IllegalArgumentException("Unknown type: " + itemDoc.getString("type"));
            }
            checkedOutItems.add(item);
        }
        account.setCheckedOutItems(checkedOutItems);
        return account;
    }
}