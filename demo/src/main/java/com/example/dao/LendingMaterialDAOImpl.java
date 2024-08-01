package com.example.dao;

import com.example.LibraryDatabaseConnection;
import com.example.models.LendingMaterial;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.mongodb.client.model.Filters;
import java.util.ArrayList;
import java.util.List;

public class LendingMaterialDAOImpl implements LendingMaterialDAO {
    private MongoDatabase database;

    public LendingMaterialDAOImpl(LibraryDatabaseConnection connection) {
        this.database = connection.getDatabase();
    }

    private MongoCollection<Document> getCollection() {
        return database.getCollection("LendingMaterial");
    }

    @Override
    public List<LendingMaterial> getAllLendingMaterials() {
        List<LendingMaterial> materials = new ArrayList<>();
        for (Document doc : getCollection().find()) {
            materials.add(fromDocument(doc));
        }
        return materials;
    }

    @Override
    public LendingMaterial getLendingMaterialById(String id) {
        Document doc = getCollection().find(Filters.eq("MaterialID", id)).first();
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
        getCollection().updateOne(Filters.eq("MaterialID", lendingMaterial.getMaterialID()), new Document("$set", toDocument(lendingMaterial)));
    }

    @Override
    public void deleteLendingMaterial(String id) {
        getCollection().deleteOne(Filters.eq("MaterialID", id));
    }

    @Override
    public void incrementLendingMaterial(LendingMaterial item) {
        item.setCopiesAvailable(item.getCopiesAvailable() + 1);
        getCollection().updateOne(
            Filters.eq("MaterialID", item.getMaterialID()),
            new Document("$set", new Document("CopiesAvailable", item.getCopiesAvailable()))
        );
    }
    

    @Override
    public void decrementLendingMaterials(List<LendingMaterial> lendingMaterials) {
        for (LendingMaterial item : lendingMaterials) {
            item.setCopiesAvailable(item.getCopiesAvailable() - 1);
            getCollection().updateOne(
                Filters.eq("MaterialID", item.getMaterialID()),
                new Document("$set", new Document("CopiesAvailable", item.getCopiesAvailable()))
            );
        }
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


            ////////////////////////////////////
            LendingMaterial lendingMaterial = mapper.readValue(json, LendingMaterial.class);
        
            // Set dates using the new setter that parses the date string
            lendingMaterial.setLendedDate(doc.getString("LendedDate"));
            lendingMaterial.setReturnDate(doc.getString("ReturnDate"));
            
            return lendingMaterial;
    

            //return mapper.readValue(json, LendingMaterial.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
