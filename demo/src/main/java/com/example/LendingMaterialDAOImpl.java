package com.example;

import com.example.models.LendingMaterial;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.mongodb.client.model.Filters;

public class LendingMaterialDAOImpl implements LendingMaterialDAO {
    private MongoDatabase database;

    public LendingMaterialDAOImpl(LibraryDatabaseConnection connection) {
        this.database = connection.getDatabase();
    }

    private MongoCollection<Document> getCollection() {
        return database.getCollection("lendingMaterials");
    }

    @Override
    public LendingMaterial getLendingMaterialById(String id) {
        Document doc = getCollection().find(Filters.eq("id", id)).first();
        if (doc != null) {
            return fromDocument(doc);
        }
        return null;
    }

    @Override
    public void insertLendingMaterial(LendingMaterial lendingMaterial) {
        getCollection().insertOne(toDocument(lendingMaterial));
    }

    @Override
    public void updateLendingMaterial(LendingMaterial lendingMaterial) {
        getCollection().updateOne(Filters.eq("id", lendingMaterial.getMaterialID()), new Document("$set", toDocument(lendingMaterial)));
    }

    @Override
    public void deleteLendingMaterial(String id) {
        getCollection().deleteOne(Filters.eq("id", id));
    }

    private Document toDocument(LendingMaterial lendingMaterial) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(lendingMaterial);
            return Document.parse(json);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private LendingMaterial fromDocument(Document doc) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = doc.toJson();
            return mapper.readValue(json, LendingMaterial.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
