package com.example.dao;

import com.example.LibraryDatabaseConnection;
import com.example.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.mongodb.client.model.Filters;

public class UserDAOImpl implements UserDAO {
    private MongoDatabase database;

    public UserDAOImpl(LibraryDatabaseConnection connection) {
        this.database = connection.getDatabase();
    }

    private MongoCollection<Document> getCollection() {
        return database.getCollection("Users");
    }

    @Override
    public User getUserById(String userId) {
        Document doc = getCollection().find(Filters.eq("UserID", userId)).first();
        if (doc != null) {
            return fromDocument(doc);
        }
        return null;
    }

    @Override
    public boolean authenticateUser(String userId, String password) {
        Document doc = getCollection().find(Filters.and(Filters.eq("UserID", userId), Filters.eq("Password", password))).first();
        if (doc != null) {
            // Print the userId and password from the document
            System.out.println("UserID from doc: " + doc.getString("UserID"));
            System.out.println("Password from doc: " + doc.getString("Password"));
            return true;
        } else {
            System.out.println("Doc is null");
            return false;
        }
    }

    @Override
    public void insertUser(User user) {
        getCollection().insertOne(toDocument(user));
    }

    @Override
    public void updateUser(User user) {
        getCollection().updateOne(Filters.eq("UserID", user.getUserId()), new Document("$set", toDocument(user)));
    }

    @Override
    public void deleteUser(int userId) {
        getCollection().deleteOne(Filters.eq("UserID", userId));
    }

    private Document toDocument(User user) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(user);
            return Document.parse(json);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private User fromDocument(Document doc) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = doc.toJson();
            return mapper.readValue(json, User.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
