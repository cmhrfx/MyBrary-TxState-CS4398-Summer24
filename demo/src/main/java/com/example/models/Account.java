package com.example.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class Account {
    private int accountId;
    private int userId;
    private String type;
    private List<LendingMaterial> checkedOutItems;
    private float balance;

    // Constructor
    public Account(int accountId, int userId, String type, float balance) {
        this.accountId = accountId;
        this.userId = userId;
        this.type = type;
        this.checkedOutItems = new ArrayList<>();
        this.balance = balance;
    }

    // Getters and setters
    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
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

    public float getbalance() {
        return balance;
    }

    public void setbalance(float balance) {
        this.balance = balance;
    }

    // Serialize the Account object to a MongoDB Document
    public Document toDocument() {
        Document doc = new Document("accountId", accountId)
                .append("userId", userId)
                .append("type", type)
                .append("checkedOutItems", serializeCheckedOutItems())
                .append("balance", balance);
        return doc;
    }

    // Deserialize a MongoDB Document to an Account object
    public static Account fromDocument(Document doc) {
        Account account = new Account(doc.getInteger("accountId"),
                                      doc.getInteger("userId"),
                                      doc.getString("type"),
                                      doc.getDouble("balance").floatValue());
        account.setCheckedOutItems(deserializeCheckedOutItems((List<Document>) doc.get("checkedOutItems")));
        return account;
    }

    // Serialize checked out items to JSON
    private String serializeCheckedOutItems() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(checkedOutItems);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Deserialize JSON to checked out items
    private static List<LendingMaterial> deserializeCheckedOutItems(List<Document> docs) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(docs);
            return mapper.readValue(json, new TypeReference<List<LendingMaterial>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
