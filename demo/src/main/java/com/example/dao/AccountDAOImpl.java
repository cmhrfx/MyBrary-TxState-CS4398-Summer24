package com.example.dao;

import com.example.LibraryDatabaseConnection;
import com.example.models.Account;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.mongodb.client.model.Filters;

public class AccountDAOImpl implements AccountDAO {
    private MongoDatabase database;

    public AccountDAOImpl(LibraryDatabaseConnection connection) {
        this.database = connection.getDatabase();
    }

    private MongoCollection<Document> getCollection() {
        return database.getCollection("Accounts");
    }

    @Override
    public Account getAccountById(String accountId) {
        Document doc = getCollection().find(Filters.eq("accountId", accountId)).first();
        if (doc != null) {
            return Account.fromDocument(doc);
        }
        return null;
    }

    @Override
    public void insertAccount(Account account) {
        getCollection().insertOne(account.toDocument());
    }

    @Override
    public void updateAccount(Account account) {
        getCollection().updateOne(Filters.eq("accountId", account.getAccountId()), new Document("$set", account.toDocument()));
    }

    @Override
    public void deleteAccount(int accountId) {
        getCollection().deleteOne(Filters.eq("accountId", accountId));
    }
}
