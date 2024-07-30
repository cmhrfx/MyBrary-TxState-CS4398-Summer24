package com.example.dao;

import com.example.LibraryDatabaseConnection;
import com.example.models.Account;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.mongodb.client.model.Filters;

import java.util.List;

public class AccountDAOImpl implements AccountDAO {
    private MongoDatabase database;

    public AccountDAOImpl(LibraryDatabaseConnection connection) {
        this.database = connection.getDatabase();
    }

    private MongoCollection<Document> getCollection() {
        return database.getCollection("Accounts");
    }

    @Override
    public Account getAccountById(int accountId) {
        Document doc = getCollection().find(Filters.eq("accountId", accountId)).first();
        if (doc != null) {
            return fromDocument(doc);
        }
        return null;
    }

    @Override
    public void insertAccount(Account account) {
        getCollection().insertOne(toDocument(account));
    }

    @Override
    public void updateAccount(Account account) {
        getCollection().updateOne(Filters.eq("accountId", account.getAccountId()), new Document("$set", toDocument(account)));
    }

    @Override
    public void deleteAccount(int accountId) {
        getCollection().deleteOne(Filters.eq("accountId", accountId));
    }

    private Document toDocument(Account account) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(account);
            return Document.parse(json);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Account fromDocument(Document doc) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = doc.toJson();
            return mapper.readValue(json, Account.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
